-- Copyright 2016-2019 Cloudera, Inc.;
-- ;
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.;
-- You may obtain a copy of the License at;
-- ;
--    http://www.apache.org/licenses/LICENSE-2.0;
-- ;
-- Unless required by applicable law or agreed to in writing, software;
-- distributed under the License is distributed on an "AS IS" BASIS,;
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.;
-- See the License for the specific language governing permissions and;
-- limitations under the License.;
-- ;

CREATE TABLE IF NOT EXISTS schema_version_state (
  id              BIGINT AUTO_INCREMENT NOT NULL,
  schemaVersionId BIGINT                NOT NULL,
  stateId         TINYINT               NOT NULL,
  sequence        INT                   NOT NULL,
  timestamp       BIGINT                NOT NULL,
  details         BLOB,
  PRIMARY KEY (schemaVersionId, stateId, sequence),
  UNIQUE KEY (id)
);

ALTER TABLE `schema_version_info` ADD `state` TINYINT NOT NULL DEFAULT 5 AFTER `timestamp`;