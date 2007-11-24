package org.apache.maven.scm.provider.git.gitexe.command.add;

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
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.add.AbstractAddCommand;
import org.apache.maven.scm.command.add.AddScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.gitexe.command.GitCommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: GitAddCommand.java 538940 2007-05-17 14:27:28Z evenisse $
 */
public class GitAddCommand
    extends AbstractAddCommand
    implements GitCommand
{
    protected ScmResult executeAddCommand( ScmProviderRepository repository, ScmFileSet fileSet, String message,
                                           boolean binary )
        throws ScmException
    {
        if ( fileSet.getFileList().isEmpty() )
        {
            throw new ScmException( "You must provide at least one file/directory to add" );
        }

        Commandline cl = createCommandLine( fileSet.getBasedir(), fileSet.getFileList() );

        GitAddConsumer consumer = new GitAddConsumer( getLogger() );

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
            return new AddScmResult( cl.toString(), "The git command failed.", stderr.getOutput(), false );
        }

        return new AddScmResult( cl.toString(), consumer.getAddedFiles() );
    }

    private static Commandline createCommandLine( File workingDirectory, List/*File*/ files )
        throws ScmException
    {
        // Base command line doesn't make sense here - username/password not needed, and non-interactive is not valid

        Commandline cl = GitCommandLineUtils.getBaseGitCommandLine( workingDirectory, "add" );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        // verbosity needed for consumer
        cl.createArgument().setValue( "-v" );
        
        try
        {
            GitCommandLineUtils.addTarget( cl, files );
        }
        catch ( IOException e )
        {
            throw new ScmException( "Can't create the targets file", e );
        }

        return cl;
    }

}
