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

package org.opencb.hpg.bigdata.core.utils;

import org.opencb.biodata.models.sequence.Read;

public final class ReadUtils {

    private ReadUtils() {
    }

    public static String getFastqString(Read read) {
        return "@" + read.getId().toString() + "\n" + read.getSequence().toString()
                + "\n+\n" + read.getQuality().toString() + "\n";
    }

}
