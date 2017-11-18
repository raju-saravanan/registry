/**
 * Copyright 2017 Hortonworks.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.hortonworks.registries.schemaregistry.util;

import com.hortonworks.registries.schemaregistry.SchemaBranchStorable;
import com.hortonworks.registries.storage.StorageManager;

public class SchemaRegistryUtil {

    public static final String MASTER_BRANCH_NAME = SchemaRegistryConstants.MASTER_BRANCH;
    public static final String MASTER_BRANCH_DESCRIPTION = "Schemas in this branch are meant to be consumed for production environment";

    public static void createMasterBranch(StorageManager jdbcStorageManager) {
        SchemaBranchStorable schemaBranchStorable = new SchemaBranchStorable(MASTER_BRANCH_NAME, MASTER_BRANCH_DESCRIPTION);
        schemaBranchStorable.setId(1l);
        jdbcStorageManager.add(schemaBranchStorable);
    }
}
