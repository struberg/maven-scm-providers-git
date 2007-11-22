package org.apache.maven.scm.provider.git.gitexe.command.changelog;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: GitChangeLogCommandTest.java 527434 2007-04-11 09:47:31Z evenisse $
 */
public class GitChangeLogCommandTest
    extends ScmTestCase
{
    public void testCommandLineNoDates()
        throws Exception
    {
        testCommandLine( "scm:git:http://foo.com/git", null, null, null,
                         "git-log" );
    }

    public void testCommandLineWithDates()
        throws Exception
    {
        Date startDate = getDate( 2003, Calendar.SEPTEMBER, 10, null );
        Date endDate = getDate( 2007, Calendar.OCTOBER, 10, null );

        testCommandLine( "scm:git:http://foo.com/git", null, startDate, endDate,
                         "git-log \"--since=2003-09-10 00:00\" \"--until=2007-10-10 00:00\"" );
    }

    public void testCommandLineStartDateOnly()
        throws Exception
    {
        Date startDate = getDate( 2003, Calendar.SEPTEMBER, 10, 1, 1, 1, GMT_TIME_ZONE );

        testCommandLine( "scm:git:http://foo.com/git", null, startDate, null,
                         "git --non-interactive log -v -r \"{2003-09-10 01:01:01 +0000}:HEAD\" http://foo.com/git" );
    }

    public void testCommandLineDateFormat()
        throws Exception
    {
        Date startDate = getDate( 2003, Calendar.SEPTEMBER, 10, 1, 1, 1, GMT_TIME_ZONE );
        Date endDate = getDate( 2005, Calendar.NOVEMBER, 13, 23, 23, 23, GMT_TIME_ZONE );

        testCommandLine( "scm:git:http://foo.com/git", null, startDate, endDate,
                         "git --non-interactive log -v -r \"{2003-09-10 01:01:01 +0000}:{2005-11-13 23:23:23 +0000}\" http://foo.com/git" );
    }

    public void testCommandLineEndDateOnly()
        throws Exception
    {
        Date endDate = getDate( 2003, Calendar.NOVEMBER, 10, GMT_TIME_ZONE );

        // Only specifying end date should print no dates at all
        testCommandLine( "scm:git:http://foo.com/git", null, null, endDate,
                         "git --non-interactive log -v http://foo.com/git" );
    }

    public void testCommandLineWithBranchNoDates()
        throws Exception
    {
        testCommandLine( "scm:git:http://foo.com/git", new ScmBranch( "my-test-branch" ), null, null,
                         "git --non-interactive log -v http://foo.com/git/branches/my-test-branch http://foo.com/git" );
    }

    public void testCommandLineWithBranchStartDateOnly()
        throws Exception
    {
        Date startDate = getDate( 2003, Calendar.SEPTEMBER, 10, 1, 1, 1, GMT_TIME_ZONE );

        testCommandLine( "scm:git:http://foo.com/git", new ScmBranch( "my-test-branch" ), startDate, null,
                         "git --non-interactive log -v -r \"{2003-09-10 01:01:01 +0000}:HEAD\" http://foo.com/git/branches/my-test-branch http://foo.com/git" );
    }

    public void testCommandLineWithBranchEndDateOnly()
        throws Exception
    {
        Date endDate = getDate( 2003, Calendar.OCTOBER, 10, 1, 1, 1, GMT_TIME_ZONE );

        // Only specifying end date should print no dates at all
        testCommandLine( "scm:git:http://foo.com/git", new ScmBranch( "my-test-branch" ), null, endDate,
                         "git --non-interactive log -v http://foo.com/git/branches/my-test-branch http://foo.com/git" );
    }

    public void testCommandLineWithBranchBothDates()
        throws Exception
    {
        Date startDate = getDate( 2003, Calendar.SEPTEMBER, 10, GMT_TIME_ZONE );
        Date endDate = getDate( 2003, Calendar.OCTOBER, 10, GMT_TIME_ZONE );

        testCommandLine( "scm:git:http://foo.com/git", new ScmBranch( "my-test-branch" ), startDate, endDate,
                         "git --non-interactive log -v -r \"{2003-09-10 00:00:00 +0000}:{2003-10-10 00:00:00 +0000}\" http://foo.com/git/branches/my-test-branch http://foo.com/git" );
    }

    public void testCommandLineWithStartVersion()
        throws Exception
    {
        testCommandLine( "scm:git:http://foo.com/git", new ScmRevision("1"), null,
                         "git --non-interactive log -v -r 1:HEAD http://foo.com/git" );
    }

    public void testCommandLineWithStartVersionAndEndVersion()
        throws Exception
    {
        testCommandLine( "scm:git:http://foo.com/git", new ScmRevision("1"), new ScmRevision("10"),
                         "git --non-interactive log -v -r 1:10 http://foo.com/git" );
    }

    public void testCommandLineWithStartVersionAndEndVersionEquals()
        throws Exception
    {
        testCommandLine( "scm:git:http://foo.com/git", new ScmRevision("1"), new ScmRevision("1"),
                         "git --non-interactive log -v -r 1 http://foo.com/git" );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void testCommandLine( String scmUrl, ScmBranch branch, Date startDate, Date endDate, String commandLine )
        throws Exception
    {
        File workingDirectory = getTestFile( "target/git-update-command-test" );

        ScmRepository repository = getScmManager().makeScmRepository( scmUrl );

        GitScmProviderRepository gitRepository = (GitScmProviderRepository) repository.getProviderRepository();

        Commandline cl = GitChangeLogCommand.createCommandLine( gitRepository, workingDirectory, branch, startDate,
                                                                endDate, null, null );

        assertEquals( commandLine, cl.toString() );
    }

    private void testCommandLine( String scmUrl, ScmVersion startVersion, ScmVersion endVersion, String commandLine )
        throws Exception
    {
        File workingDirectory = getTestFile( "target/git-update-command-test" );

        ScmRepository repository = getScmManager().makeScmRepository( scmUrl );

        GitScmProviderRepository gitRepository = (GitScmProviderRepository) repository.getProviderRepository();

        Commandline cl = GitChangeLogCommand.createCommandLine( gitRepository, workingDirectory, null, null, null,
                                                                startVersion, endVersion );

        assertEquals( commandLine, cl.toString() );
    }
}
