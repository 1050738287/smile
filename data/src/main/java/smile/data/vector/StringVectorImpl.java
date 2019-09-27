/*******************************************************************************
 * Copyright (c) 2010-2019 Haifeng Li
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package smile.data.vector;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import smile.data.measure.DiscreteMeasure;
import smile.data.measure.NominalScale;
import smile.data.type.DataTypes;

/**
 * An immutable string vector.
 *
 * @author Haifeng Li
 */
class StringVectorImpl extends VectorImpl<String> implements StringVector {

    /** Constructor. */
    public StringVectorImpl(String name, String[] vector) {
        super(name, String.class, vector);
    }

    @Override
    public StringVector get(int... index) {
        String[] v = new String[index.length];
        for (int i = 0; i < index.length; i++) v[i] = get(index[i]);
        return new StringVectorImpl(name(), v);
    }

    @Override
    public Vector<LocalDate> toDate() {
        return toDate(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public Vector<LocalDate> toDate(DateTimeFormatter format) {
        LocalDate[] dates = stream().map(s -> format.parse(s)).toArray(LocalDate[]::new);
        return new VectorImpl<>(name(), DataTypes.DateType, dates);
    }

    @Override
    public Vector<LocalTime> toTime() {
        return toTime(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Override
    public Vector<LocalTime> toTime(DateTimeFormatter format) {
        LocalTime[] dates = stream().map(s -> format.parse(s)).toArray(LocalTime[]::new);
        return new VectorImpl<>(name(), DataTypes.TimeType, dates);
    }

    @Override
    public Vector<LocalDateTime> toDateTime() {
        return toDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public Vector<LocalDateTime> toDateTime(DateTimeFormatter format) {
        LocalDateTime[] dates = stream().map(s -> format.parse(s)).toArray(LocalDateTime[]::new);
        return new VectorImpl<>(name(), DataTypes.DateTimeType, dates);
    }

    @Override
    public NominalScale nominal() {
        List<String> levels = distinct();
        Collections.sort(levels);
        return new NominalScale(levels);
    }

    @Override
    public BaseVector factorize(DiscreteMeasure scale) {
        int[] data = stream().mapToInt(s -> s == null ? -1 : (int) scale.valueOf(s)).toArray();

        switch (scale.type().id()) {
            case Byte:
                byte[] bytes = new byte[data.length];
                System.arraycopy(data, 0, bytes, 0, data.length);
                return new ByteVectorImpl(name(), bytes);
            case Short:
                short[] shorts = new short[data.length];
                System.arraycopy(data, 0, shorts, 0, data.length);
                return new ShortVectorImpl(name(), shorts);
            case Integer:
                return new IntVectorImpl(name(), data);
            default:
                // we should never reach here.
                throw new UnsupportedOperationException("Unsupported data type for nominal measure: " + scale.type());
        }
    }
}