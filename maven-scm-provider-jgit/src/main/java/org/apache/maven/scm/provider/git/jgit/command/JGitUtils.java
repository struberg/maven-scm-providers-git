package org.apache.maven.scm.provider.git.jgit.command;

import java.io.File;
import java.util.List;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.log.ScmLogger;
import org.spearce.jgit.lib.ProgressMonitor;
import org.spearce.jgit.lib.TextProgressMonitor;
import org.spearce.jgit.simple.SimpleRepository;
import org.spearce.jgit.simple.LsFileEntry.FileStatus;

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


/**
 * JGit SimpleRepository utility functions.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 * @version $Id: JGitUtils.java  $
 */
public class JGitUtils
{

    /**
     * Construct a logging ProgressMonitor for all JGit operations.
     * @param logger
     * @return a ProgressMonitor for use in {@code SimpleRepository}
     */
    public static ProgressMonitor getMonitor(ScmLogger logger) 
    {
        //X TODO write an own ProgressMonitor which logs to ScmLogger!
        return new TextProgressMonitor();
    }

    
    /**
     * Translate a {@code FileStatus} in the matching {@code ScmFileStatus}.
     * 
     * @param status
     * @return the matching ScmFileStatus
     * @throws ScmException if the given Status cannot be translated
     */
    public static ScmFileStatus getScmFileStatus( FileStatus status ) 
    throws ScmException {
        switch (status) {
            case CACHED: 
                return ScmFileStatus.CHECKED_IN;
            case CHANGED:
                return ScmFileStatus.MODIFIED;
            case UNMERGED:
                return ScmFileStatus.CONFLICT;
            case REMOVED:
                return ScmFileStatus.DELETED;
            case KILLED:
                return ScmFileStatus.CONFLICT;
            case OTHER:
                return ScmFileStatus.ADDED;
            default:
                throw new ScmException("unknown FileStatus: " + status);
                 
        }
    }
    
    /**
     * get the branch name from the ScmVersion
     * @param scmVersion
     * @return branch name if the ScmVersion indicates a branch, <code>&quot;master&quot;</code> otherwise
     */
    public static String getBranchName( ScmVersion scmVersion )
    {
        String branchName = "master";
        if (scmVersion instanceof ScmBranch)
        {
            branchName = scmVersion.getName();
        }
        
        return branchName;
    }
    
    /**
     * Add all files of the given fileSet to the SimpleRepository.
     * This will make all relative paths be under the repositories base directory. 
     * 
     * @param srep
     * @param fileSet
     * @throws Exception
     */
    public static void addAllFiles( SimpleRepository srep, ScmFileSet fileSet ) throws Exception {
        @SuppressWarnings("unchecked")
        List<File> addFiles = fileSet.getFileList();
        if ( addFiles != null )
        {
            for ( File addFile : addFiles )
            {
                if ( !addFile.isAbsolute() )
                {
                    addFile = new File( fileSet.getBasedir(), addFile.getPath() );
                }
                
                srep.add( addFile, false );
            }

        }
    }

}
