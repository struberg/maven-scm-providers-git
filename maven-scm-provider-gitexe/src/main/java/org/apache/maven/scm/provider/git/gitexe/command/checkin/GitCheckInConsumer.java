package org.apache.maven.scm.provider.git.gitexe.command.checkin;

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
import org.apache.maven.scm.provider.git.gitexe.command.AbstractFileCheckingConsumer;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: GitCheckInConsumer.java 538861 2007-05-17 10:08:06Z evenisse $
 */
public class GitCheckInConsumer
    extends AbstractFileCheckingConsumer
{

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public GitCheckInConsumer( ScmLogger logger, File workingDirectory )
    {
        super( logger, workingDirectory );
    }

    // ----------------------------------------------------------------------
    // StreamConsumer Implementation
    // ----------------------------------------------------------------------

    protected void parseLine( String line )
    {
        String file;

   
    }

    public List getCheckedInFiles()
    {
        return getFiles();
    }
}
