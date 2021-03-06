/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.hpg.bigdata.core.lib;

import org.opencb.commons.datastore.core.Query;

import java.util.List;

/**
 * Created by imedina on 04/08/16.
 */
public class VariantDataset extends ParentDataset<VariantDataset> {

    private VariantParseQuery variantParseQuery;
    private String sql;

    public VariantDataset() {
        variantParseQuery = new VariantParseQuery();
    }

    @Override
    protected void updateDataset(Query query) {
        if (this.sql == null) {
            this.sql = variantParseQuery.parse(query, null, viewName);
            this.ds = this.sqlContext.sql(this.sql);

            for (String item: variantParseQuery.getExplodes()) {
                String[] fields = item.split(" as ");
                this.ds = this.ds.drop(fields[1]);
            }
            this.ds = this.ds.dropDuplicates("id");
        }
    }

    private void updateQuery(String key, List<String> values, boolean and) {
        if (and) {
            query.put(key, String.join(";", values));
        } else {
            query.put(key, String.join(",", values));
        }
    }


    // id filter
    public VariantDataset idFilter(String value) {
        query.put("id", value);
        return this;
    }

    public VariantDataset idFilter(List<String> values) {
        return idFilter(values, false);
    }

    public VariantDataset idFilter(List<String> values, boolean and) {
        updateQuery("id", values, and);
        return this;
    }

    // type filter
    public VariantDataset typeFilter(String value) {
        query.put("type", value);
        return this;
    }

    public VariantDataset typeFilter(List<String> values) {
        updateQuery("type", values, false);
        return this;
    }

    // region filter
    // has been moved to the ParentDataset

    // study filter
    public VariantDataset studyFilter(String key, String value) {
        query.put("studies." + key, value);
        return this;
    }

    public VariantDataset studyFilter(String key, List<String> values) {
        return studyFilter(key, values, false);
    }

    public VariantDataset studyFilter(String key, List<String> values, boolean and) {
        updateQuery("studies." + key, values, and);
        return this;
    }

    // annotation filter
    public VariantDataset annotationFilter(String key, String value) {
        query.put("annotation." + key, value);
        return this;
    }

    public VariantDataset annotationFilter(String key, List<String> values) {
        return annotationFilter(key, values, false);
    }

    public VariantDataset annotationFilter(String key, List<String> values, boolean and) {
        updateQuery("annotation." + key, values, and);
        return this;
    }
}
