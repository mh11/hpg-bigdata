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
import org.opencb.commons.datastore.core.QueryOptions;

import java.util.Set;

/**
 * Created by jtarraga on 19/08/16.
 */
public class AlignmentParseQuery extends ParseQuery {

    public AlignmentParseQuery() {
        super();
    }

    public String parse(Query query, QueryOptions queryOptions, String viewName) {

        Set<String> keySet = query.keySet();
        for (String key : keySet) {
            String[] fields = key.split("\\.");

            // First we check if there is any ...
            if (fields.length == 0) {
                return null;
            }

            String value = (String) query.get(key);

            switch (fields[0]) {

                case "mapq": // mapping quality
                    filters.add(processFilter("alignment.mappingQuality", value, false, false));
                    break;
                case "tlen": // template length = insert size
                    filters.add(processFilter("abs(fragmentLength)", value, false, false));
                    break;
                case "alen": // alignment length
                    filters.add(processFilter("length(alignedSequence)", value, false, false));
                    break;
                case "required-flag":
                    processFlagQuery(value, false);
                    break;
                case "filtering-flag":
                    processFlagQuery(value, true);
                    break;
                case "region":
                    processRegionQuery(value, "alignment.position.referenceName",
                            "alignment.position.position", "(alignment.position.position + length(alignedSequence))");
                    break;
                default:
                    break;
            }
        }

        // Build the SQL string from the processed query using explodes and filters
        buildQueryString(viewName, queryOptions);

        return sqlQueryString.toString();
    }



    private void processFlagQuery(String value, Boolean exclude) {

        String[] values = value.split("[,;]");

        int flag = 0;
        for (int i = 0; i < values.length; i++) {
            flag |= Integer.parseInt(values[i]);
        }

        if (exclude) {
            flag ^= 0xFFFF;
        }
        //System.out.println("---------> flag = " + flag);

        if ((flag & 1) > 0) {
            // template having multiple segments in sequencing
            filters.add("(numberReads > 0)");
        }
        if ((flag & 2) > 0) {
            // each segment properly aligned according to the aligner
            filters.add("(improperPlacement = true)");
        }
        if ((flag & 4) > 0) {
            // segment unmapped
            filters.add("(numberReads = 0)");
        }
        if ((flag & 8) > 0) {
            // next segment in the template unmapped
            filters.add("(numberReads > 0 AND nextMatePosition IS NULL)");
        }
        if ((flag & 16) > 0) {
            // SEQ being reverse complemented
            filters.add("(numberReads > 0 AND alignment.position.strand = 'NEG_STRAND')");
        }
        if ((flag & 32) > 0) {
            // SEQ of the next segment in the template being reverse complemented
            filters.add("(nextMatePosition.strand = 'NEG_STRAND')");
        }
        if ((flag & 64) > 0) {
            // the first segment in the template
            filters.add("(numberReads > 0 AND readNumber = 0)");
        }
        if ((flag & 128) > 0) {
            // the last segment in the template
            filters.add("(numberReads > 0 AND readNumber = numberReads - 1)");
            //System.exit(-1);
        }
        if ((flag & 256) > 0) {
            // secondary alignment
            filters.add("(secondaryAlignment = true)");
        }
        if ((flag & 512) > 0) {
            // not passing  lters, such as platform/vendor quality controls
            filters.add("(failedVendorQualityChecks = true)");
        }
        if ((flag & 1024) > 0) {
            // PCR or optical duplicate
            filters.add("(duplicateFragment = true)");
        }
        if ((flag & 2048) > 0) {
            // supplementary alignment
            filters.add("(supplementaryAlignment = true)");
        }
    }
}

