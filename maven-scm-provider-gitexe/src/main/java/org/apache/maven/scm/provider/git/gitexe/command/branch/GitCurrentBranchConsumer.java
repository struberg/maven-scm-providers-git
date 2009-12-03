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

import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.util.AbstractConsumer;

/**
 * This logger parses the output of <i>git symbolic-ref HEAD</i>
 * to determine the current branch.
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 * @version $Id: GitCurrentBranchConsumer.java 823147 2009-10-08 12:39:23Z struberg $
 */
public class GitCurrentBranchConsumer
    extends AbstractConsumer
{
    
    private final static String BRANCH_INDICATOR = "refs/heads/";
    
    private String branch;
    
    /**
     * Default constructor.
     */
    public GitCurrentBranchConsumer( ScmLogger logger )
    {
        super( logger );
    }

    public String getBranchName()
    {
        return branch;
    }

    // ----------------------------------------------------------------------
    // StreamConsumer Implementation
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void consumeLine( String line )
    {
        line = line.trim();
        
        if ( line.startsWith( BRANCH_INDICATOR ) )
        {
            branch = line.substring( BRANCH_INDICATOR.length() );
        }
    }

}