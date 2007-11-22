package org.apache.maven.scm.provider.git.gitexe.command;

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

import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.git.util.GitUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

/**
 * Command line construction utility.
 *
 * @author Brett Porter
 * @version $Id: GitCommandLineUtils.java 579982 2007-09-27 12:09:20Z evenisse $
 */
public class GitCommandLineUtils
{
    public static void addTarget( Commandline cl, List/*<File>*/ files )
        throws IOException
    {
        if ( files == null || files.isEmpty() )
        {
            return;
        }

        StringBuffer sb = new StringBuffer();
        String ls = System.getProperty( "line.separator" );
        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            File f = (File) i.next();
            sb.append( f.getPath().replace( '\\', '/' ) );
            sb.append( ls );
        }

        File targets = File.createTempFile( "maven-scm-", "-targets" );
        PrintStream out = new PrintStream( new FileOutputStream( targets ) );
        out.print( sb.toString() );
        out.flush();
        out.close();

        cl.createArgument().setValue( "--targets" );
        cl.createArgument().setValue( targets.getAbsolutePath() );

        targets.deleteOnExit();
    }

    public static Commandline getBaseGitCommandLine( File workingDirectory, String command )
    {
        if ( command == null || command.length() == 0) 
        {
            return null;
        }
        
        Commandline cl = new Commandline();

        cl.setExecutable( "git" + "-" + command );

        cl.setWorkingDirectory( workingDirectory.getAbsolutePath() );

        return cl;
    }

    public static int execute( Commandline cl, StreamConsumer consumer, CommandLineUtils.StringStreamConsumer stderr,
                               ScmLogger logger )
        throws CommandLineException
    {
        int exitCode = CommandLineUtils.executeCommandLine( cl, consumer, stderr );

        exitCode = checkIfCleanUpIsNeeded( exitCode, cl, consumer, stderr, logger );

        return exitCode;
    }

    public static int execute( Commandline cl, CommandLineUtils.StringStreamConsumer stdout,
                               CommandLineUtils.StringStreamConsumer stderr, ScmLogger logger )
        throws CommandLineException
    {
        int exitCode = CommandLineUtils.executeCommandLine( cl, stdout, stderr );

        exitCode = checkIfCleanUpIsNeeded( exitCode, cl, stdout, stderr, logger );

        return exitCode;
    }

    private static int checkIfCleanUpIsNeeded( int exitCode, Commandline cl, StreamConsumer consumer,
                                               CommandLineUtils.StringStreamConsumer stderr, ScmLogger logger )
        throws CommandLineException
    {
        if ( exitCode != 0 && stderr.getOutput() != null && stderr.getOutput().indexOf( "'git cleanup'" ) > 0 &&
            stderr.getOutput().indexOf( "'git help cleanup'" ) > 0 )
        {
            logger.info( "Git command failed due to some locks in working copy. We try to run a 'git cleanup'." );

            if ( executeCleanUp( cl.getWorkingDirectory(), consumer, stderr, logger ) == 0 )
            {
                exitCode = CommandLineUtils.executeCommandLine( cl, consumer, stderr );
            }
        }
        return exitCode;
    }

    public static int executeCleanUp( File workinDirectory, StreamConsumer stdout, StreamConsumer stderr )
        throws CommandLineException
    {
        return executeCleanUp( workinDirectory, stdout, stderr, null );
    }

    public static int executeCleanUp( File workinDirectory, StreamConsumer stdout, StreamConsumer stderr,
                                      ScmLogger logger )
        throws CommandLineException
    {
        Commandline cl = new Commandline();

        cl.setExecutable( "git" );

        cl.setWorkingDirectory( workinDirectory.getAbsolutePath() );

        if ( logger != null )
        {
            logger.info( "Executing: " + cl );
            logger.info( "Working directory: " + cl.getWorkingDirectory().getAbsolutePath() );
        }

        return CommandLineUtils.executeCommandLine( cl, stdout, stderr );
    }


}
