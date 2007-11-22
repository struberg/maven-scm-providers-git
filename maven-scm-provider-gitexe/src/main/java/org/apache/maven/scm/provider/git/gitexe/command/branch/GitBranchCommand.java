package org.apache.maven.scm.provider.git.gitexe.command.branch;

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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.branch.AbstractBranchCommand;
import org.apache.maven.scm.command.branch.BranchScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.apache.maven.scm.provider.git.gitexe.command.GitCommandLineUtils;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: GitBranchCommand.java 531990 2007-04-24 15:55:06Z evenisse $
 * @todo since this is just a copy, use that instead.
 */
public class GitBranchCommand
    extends AbstractBranchCommand
    implements GitCommand
{
    public ScmResult executeBranchCommand( ScmProviderRepository repo, ScmFileSet fileSet, String branch,
                                           String message )
        throws ScmException
    {
        if ( branch == null || StringUtils.isEmpty( branch.trim() ) )
        {
            throw new ScmException( "branch name must be specified" );
        }

        if ( fileSet.getFiles().length != 0 )
        {
            throw new ScmException( "This provider doesn't support branching subsets of a directory" );
        }

        GitScmProviderRepository repository = (GitScmProviderRepository) repo;

        Commandline cl = createCommandLine( repository, fileSet.getBasedir(), branch );

        CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        getLogger().info( "Executing: " + cl );
        getLogger().info( "Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );

        int exitCode;

        try
        {
            exitCode = GitCommandLineUtils.execute( cl, stdout, stderr, getLogger() );
        }
        catch ( CommandLineException ex )
        {
            throw new ScmException( "Error while executing command.", ex );
        }

        if ( exitCode != 0 )
        {
            return new BranchScmResult( cl.toString(), "The git branch command failed.", stderr.getOutput(), false );
        }

        List fileList = new ArrayList();

        List files = null;

        try
        {
            files = FileUtils.getFiles( fileSet.getBasedir(), "**", "**/.git/**", false );
        }
        catch ( IOException e )
        {
            throw new ScmException( "Error while executing command.", e );
        }

        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            File f = (File) i.next();

            fileList.add( new ScmFile( f.getPath(), ScmFileStatus.TAGGED ) );
        }

        return new BranchScmResult( cl.toString(), fileList );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static Commandline createCommandLine( GitScmProviderRepository repository, File workingDirectory,
                                                 String branch )
    {
        Commandline cl = GitCommandLineUtils.getBaseGitCommandLine( workingDirectory, "branch" );

        cl.createArgument().setValue( branch );

        return cl;
    }
}
