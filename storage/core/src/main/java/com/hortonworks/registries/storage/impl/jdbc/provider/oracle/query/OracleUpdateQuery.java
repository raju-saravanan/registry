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

package com.hortonworks.registries.storage.impl.jdbc.provider.oracle.query;

import com.hortonworks.registries.common.Schema;
import com.hortonworks.registries.storage.Storable;
import com.hortonworks.registries.storage.impl.jdbc.provider.oracle.exception.OracleQueryException;
import com.hortonworks.registries.storage.impl.jdbc.provider.sql.query.AbstractStorableUpdateQuery;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OracleUpdateQuery extends AbstractStorableUpdateQuery {

    private Map<Schema.Field, Object> whereClauseColumnToValueMap = new HashMap<>();

    public OracleUpdateQuery(Storable storable) {
        super(storable);
        whereClauseColumnToValueMap = createWhereClauseColumnToValueMap();
    }

    @Override
    protected String createParameterizedSql() {

        List<String> whereClauseColumns = new LinkedList<>();
        for (Map.Entry<Schema.Field, Object> columnKeyValue : whereClauseColumnToValueMap.entrySet()) {
            if (columnKeyValue.getKey().getType() == Schema.Type.STRING) {
                String stringValue = (String) columnKeyValue.getValue();
                if ((stringValue).length() > 4000) {
                    throw new OracleQueryException(String.format("Column \"%s\" of the table \"%s\" is compared against a value \"%s\", " +
                                    "which is greater than 4k characters",
                            columnKeyValue.getKey().getName(), tableName, stringValue));
                } else
                    whereClauseColumns.add(String.format(" to_char(\"%s\") = ?", columnKeyValue.getKey().getName()));
            } else {
                whereClauseColumns.add(String.format(" \"%s\" = ?", columnKeyValue.getKey().getName()));
            }
        }

        String sql = "UPDATE \"" + tableName + "\" SET "
                + join(getNonPrimaryColumns(getStorable(), "\"%s\" = ?"), ", ")
                + " WHERE " + join(whereClauseColumns, " AND ");
        LOG.debug("Sql '{}'", sql);
        return sql;
    }

    public List<Pair<Schema.Field, Object>> getBindings() {
        List<Pair<Schema.Field, Object>> uniqueBindings = new ArrayList<>();
        Map<String, Pair<Schema.Field, Object>> bindingsMap = new HashMap<>();
        for (Pair<Schema.Field, Object> binding : super.getBindings()) {
            bindingsMap.put(binding.getKey().getName(), Pair.of(binding.getKey(), binding.getValue()));
        }

        for (String columnName : getNonPrimaryColumns(getStorable(), "%s")) {
            uniqueBindings.add(bindingsMap.get(columnName));
        }

        for (String columnName : getPrimaryColumns(getStorable(), "%s")) {
            uniqueBindings.add(bindingsMap.get(columnName));
        }

        return uniqueBindings;
    }

    private List<String> getPrimaryColumns(Storable storable, final String formatter) {
        return storable.getPrimaryKey().getFieldsToVal().keySet().stream().map(
                colField -> String.format(formatter, colField.getName())).collect(Collectors.toList());
    }

    private List<String> getNonPrimaryColumns(Storable storable, final String formatter) {
        Set<String> primaryKeySet = new HashSet();
        primaryKeySet.addAll(getPrimaryColumns(storable, formatter));
        Collection<String> columnNames = getColumnNames(columns, formatter);
        return columnNames.stream().filter(colField -> !primaryKeySet.contains(colField)).collect(Collectors.toList());
    }


    private Map<Schema.Field, Object> createWhereClauseColumnToValueMap() {
        Map<Schema.Field, Object> bindingMap = new HashMap<>();
        for (Pair<Schema.Field, Object> fieldObjectPair : getBindings()) {
            bindingMap.put(fieldObjectPair.getKey(), fieldObjectPair.getValue());
        }
        return whereFields.stream().collect(Collectors.toMap(f -> f, f -> bindingMap.get(f)));
    }
}
