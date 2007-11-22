package org.apache.maven.scm.provider.git.gitexe.command.diff;

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
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.diff.AbstractDiffCommand;
import org.apache.maven.scm.command.diff.DiffScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.command.diff.GitDiffConsumer;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.apache.maven.scm.provider.git.gitexe.command.GitCommandLineUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

/**
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: GitDiffCommand.java 524909 2007-04-02 20:02:44Z evenisse $
 */
public class GitDiffCommand
    extends AbstractDiffCommand
    implements GitCommand
{
    protected DiffScmResult executeDiffCommand( ScmProviderRepository repo, ScmFileSet fileSet, ScmVersion startVersion,
                                                ScmVersion endVersion )
        throws ScmException
    {
        Commandline cl =
            createCommandLine( (GitScmProviderRepository) repo, fileSet.getBasedir(), startVersion, endVersion );

        GitDiffConsumer consumer = new GitDiffConsumer( getLogger(), fileSet.getBasedir() );

        CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

        getLogger().info( "Executing: " + cl );
        getLogger().info( "Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );

        int exitCode;

        try
        {
            exitCode = GitCommandLineUtils.execute( cl, consumer, stderr, getLogger() );
        }
        catch ( CommandLineException ex )
        {
            throw new ScmException( "Error while executing command.", ex );
        }

        if ( exitCode != 0 )
        {
            return new DiffScmResult( cl.toString(), "The git command failed.", stderr.getOutput(), false );
        }

        return new DiffScmResult( cl.toString(), consumer.getChangedFiles(), consumer.getDifferences(),
                                  consumer.getPatch() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static Commandline createCommandLine( GitScmProviderRepository repository, File workingDirectory,
                                                 ScmVersion startVersion, ScmVersion endVersion )
    {
        Commandline cl = GitCommandLineUtils.getBaseGitCommandLine( workingDirectory, null );

        cl.createArgument().setValue( "diff" );

        if ( startVersion != null && StringUtils.isNotEmpty( startVersion.getName() ) )
        {
            cl.createArgument().setValue( "-r" );

            if ( endVersion != null && StringUtils.isNotEmpty( endVersion.getName() ) )
            {
                cl.createArgument().setValue( startVersion.getName() + ":" + endVersion.getName() );
            }
            else
            {
                cl.createArgument().setValue( startVersion.getName() );
            }
        }

        return cl;
    }
}
