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

package org.opencb.hpg.bigdata.core.parquet;

import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.avro.VariantAvro;

/**
 * Created by imedina on 02/08/16.
 */
public class VariantParquetConverter extends ParquetConverter<VariantAvro> {

    public VariantParquetConverter() {
        this(CompressionCodecName.GZIP, 128 * 1024 * 1024, 128 * 1024);
    }

    public VariantParquetConverter(CompressionCodecName compressionCodecName, int rowGroupSize, int pageSize) {
        super(compressionCodecName, rowGroupSize, pageSize);

        this.schema = VariantAvro.SCHEMA$;
    }

    public VariantParquetConverter addRegionFilter(Region region) {
        getFilters().add(v -> v.getChromosome().equals(region.getChromosome())
                && v.getEnd() >= region.getStart()
                && v.getStart() <= region.getEnd());
        return this;
    }

}
