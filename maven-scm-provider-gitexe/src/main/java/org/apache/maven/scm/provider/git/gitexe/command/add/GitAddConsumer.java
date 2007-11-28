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

import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.log.ScmLogger;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: GitAddConsumer.java 483105 2006-12-06 15:07:54Z evenisse $
 */
public class GitAddConsumer
    implements StreamConsumer
{
    private ScmLogger logger;

    private List addedFiles = new ArrayList();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public GitAddConsumer( ScmLogger logger )
    {
        this.logger = logger;
    }

    // ----------------------------------------------------------------------
    // StreamConsumer Implementation
    // ----------------------------------------------------------------------

    public void consumeLine( String line )
    {
        if ( line == null || line.length() == 0 )
        {
            logger.warn( "got empty line. seems there is nothing to do!" );

            return;
        }

        ScmFileStatus status;
        String file = null;

        if ( line.startsWith( "add '" ) )
        {
            status = ScmFileStatus.ADDED;
            file = line.substring( 5, line.length()-1 );
        }
        else
        {
            logger.info( "Unknown git-add status: '" + line + "'." );

            return;
        }

        addedFiles.add( new ScmFile( file, status ) );
    }

    public List getAddedFiles()
    {
        return addedFiles;
    }

}
