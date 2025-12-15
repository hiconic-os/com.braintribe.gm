package com.braintribe.gm.graphfetching.test.gen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.braintribe.gm.graphfetching.test.model.tech.Entitya;
import com.braintribe.gm.graphfetching.test.model.tech.Entityb;
import com.braintribe.gm.graphfetching.test.model.tech.Entityc;
import com.braintribe.gm.graphfetching.test.model.tech.Entityd;
import com.braintribe.gm.graphfetching.test.model.tech.Entitye;
import com.braintribe.gm.graphfetching.test.model.tech.Entityf;
import com.braintribe.gm.graphfetching.test.model.tech.Entityg;
import com.braintribe.gm.graphfetching.test.model.tech.Entityh;
import com.braintribe.gm.graphfetching.test.model.tech.Entityi;
import com.braintribe.gm.graphfetching.test.model.tech.Entityj;
import com.braintribe.gm.graphfetching.test.model.tech.Enuma;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.CollectionType.CollectionKind;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.LinearCollectionType;
import com.braintribe.model.generic.reflection.MapType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;

/**
 * Generator für Testmassendaten für die Entities:
 * Entitya ... Entityj (alle HasScalars-basiert).
 *
 * - Erzeugt pro Typ einen Pool von Instanzen
 * - Vergibt IDs (konfigurierbar) + globalId (immer)
 * - Füllt Scalar-Properties deterministisch
 * - Vernetzt Entity-Refs + Collections deterministisch "hash-random"
 * - Optional: Null-Werte für Scalars, TO_ONE und Collections
 *
 * Wichtig:
 * - Kein globaler Random-State, alles ist Hash-basiert.
 * - Änderungen am Modell (neue Properties/Typen) haben möglichst nur lokale Effekte.
 */
public class TechDataGenerator {

    public enum IdMode {
        NONE,
        STRING,
        LONG
    }

    public static class Config {
        private IdMode idMode = IdMode.LONG;
        private int countPerType = 1_000;
        private long seed = 1L;

        private int scalarCollectionSize = 5;
        private int entityCollectionSize = 5;
        private int mapSize = 5;

        // fix laut Vorgabe
        private double mapCollisionRate = 0.1;
        
        private String partition;

        // === Null generation (all default off) ===
        private double nullRateToOne = 0.0;          // chance that a TO_ONE relation becomes null
        private double emptyRateCollection = 0.0;    // chance that a collection is empty instead of filled
        private double nullRateScalars = 0.0;        // chance scalar becomes null (only eligible types)

        public IdMode getIdMode() {
            return idMode;
        }
        public Config setIdMode(IdMode idMode) {
            this.idMode = idMode;
            return this;
        }

        public int getCountPerType() {
            return countPerType;
        }
        public Config setCountPerType(int countPerType) {
            this.countPerType = countPerType;
            return this;
        }

        public long getSeed() {
            return seed;
        }
        public Config setSeed(long seed) {
            this.seed = seed;
            return this;
        }
        
        public void setPartition(String partition) {
			this.partition = partition;
		}

        public int getScalarCollectionSize() {
            return scalarCollectionSize;
        }
        public Config setScalarCollectionSize(int scalarCollectionSize) {
            this.scalarCollectionSize = scalarCollectionSize;
            return this;
        }

        public int getEntityCollectionSize() {
            return entityCollectionSize;
        }
        public Config setEntityCollectionSize(int entityCollectionSize) {
            this.entityCollectionSize = entityCollectionSize;
            return this;
        }

        public int getMapSize() {
            return mapSize;
        }
        public Config setMapSize(int mapSize) {
            this.mapSize = mapSize;
            return this;
        }

        public double getMapCollisionRate() {
            return mapCollisionRate;
        }

        public double getNullRateToOne() {
            return nullRateToOne;
        }
        public Config setNullRateToOne(double nullRateToOne) {
            this.nullRateToOne = nullRateToOne;
            return this;
        }

        public double getEmptyRateCollection() {
            return emptyRateCollection;
        }
        public Config setEmptyRateCollection(double emptyRateCollection) {
            this.emptyRateCollection = emptyRateCollection;
            return this;
        }

        public double getNullRateScalars() {
            return nullRateScalars;
        }
        public Config setNullRateScalars(double nullRateScalars) {
            this.nullRateScalars = nullRateScalars;
            return this;
        }
    }

    @FunctionalInterface
    public interface EntitySupplier {
        <T extends GenericEntity> T create(EntityType<T> type);
    }

    private final Config config;
    private final EntitySupplier entitySupplier;

    private final Map<EntityType<?>, String> typeShortNames = new LinkedHashMap<>();
    private Map<EntityType<?>, List<GenericEntity>> pool = new LinkedHashMap<>();

    // Rollen-Konstanten für Random-Herleitung (nur zur Entkopplung)
    private static final int ROLE_SCALAR_VALUE      = 1;
    private static final int ROLE_SCALAR_NULL       = 2;
    private static final int ROLE_TO_ONE_PICK       = 3;
    private static final int ROLE_TO_ONE_NULL       = 4;
    private static final int ROLE_COLL_EMPTY        = 6;
    private static final int ROLE_COLL_ENTITY_PICK  = 7;
    private static final int ROLE_COLL_SCALAR_VALUE = 8;
    private static final int ROLE_MAP_KEY           = 9;
    private static final int ROLE_MAP_VALUE         = 10;
    private static final int ROLE_MAP_COLLISION     = 11;

    public TechDataGenerator(Config config, EntitySupplier entitySupplier) {
        this.config = config != null ? config : new Config();
        this.entitySupplier = entitySupplier;
        initShortNames();
    }

    private void initShortNames() {
        typeShortNames.put(Entitya.T, "a");
        typeShortNames.put(Entityb.T, "b");
        typeShortNames.put(Entityc.T, "c");
        typeShortNames.put(Entityd.T, "d");
        typeShortNames.put(Entitye.T, "e");
        typeShortNames.put(Entityf.T, "f");
        typeShortNames.put(Entityg.T, "g");
        typeShortNames.put(Entityh.T, "h");
        typeShortNames.put(Entityi.T, "i");
        typeShortNames.put(Entityj.T, "j");
    }

    public Map<EntityType<?>, List<GenericEntity>> generateAll() {
        createPoolForType(Entitya.T, pool);
        createPoolForType(Entityb.T, pool);
        createPoolForType(Entityc.T, pool);
        createPoolForType(Entityd.T, pool);
        createPoolForType(Entitye.T, pool);
        createPoolForType(Entityf.T, pool);
        createPoolForType(Entityg.T, pool);
        createPoolForType(Entityh.T, pool);
        createPoolForType(Entityi.T, pool);
        createPoolForType(Entityj.T, pool);

        fillScalarProperties(pool);
        linkRelations(pool);

        return pool;
    }
    
    public Map<EntityType<?>, List<GenericEntity>> getPool() {
		return pool;
	}
    
    private <T extends GenericEntity> void createPoolForType(EntityType<T> type,
                                                             Map<EntityType<?>, List<GenericEntity>> pool) {
        List<GenericEntity> list = new ArrayList<>(config.getCountPerType());
        String shortName = typeShortNames.getOrDefault(type, type.getShortName());
        
        long idSequence = 0;

        for (int i = 0; i < config.getCountPerType(); i++) {
            T entity = entitySupplier.create(type);
            String globalId = shortName + "-" + (i + 1L);

            entity.setGlobalId(globalId);
            
        	if (config.partition != null)
        		entity.setPartition(config.partition);
        	
            IdMode mode = config.getIdMode();

            switch (mode) {
                case STRING: entity.setId(globalId); break;
                case LONG: entity.setId(++idSequence); break;
                default: break;
            }

            list.add(entity);
        }
        pool.put(type, list);
    }

    // ---------------- Scalar-Füllung ----------------

    private void fillScalarProperties(Map<EntityType<?>, List<GenericEntity>> pool) {
        for (Map.Entry<EntityType<?>, List<GenericEntity>> e : pool.entrySet()) {
            EntityType<?> type = e.getKey();
            List<GenericEntity> entities = e.getValue();
            String typeShort = typeShortNames.getOrDefault(type, type.getShortName());

            for (int entityIndex = 0; entityIndex < entities.size(); entityIndex++) {
                GenericEntity entity = entities.get(entityIndex);

                for (Property p : type.getProperties()) {
                    GenericModelType gmt = p.getType();
                    if (p.isIdentifying() || p.isGlobalId() || !gmt.isScalar()) {
                        continue;
                    }

                    boolean assignNull = false;
                    if (config.getNullRateScalars() > 0 && isNullableScalar(gmt)) {
                        double rndNull = randomDouble(typeShort, p.getName(), entityIndex, ROLE_SCALAR_NULL, 0);
                        if (rndNull < config.getNullRateScalars()) {
                            assignNull = true;
                        }
                    }

                    Object value;
                    if (assignNull) {
                        value = null;
                    } else {
                        long scalarSeed = scalarSeed(typeShort, p.getName(), entityIndex, ROLE_SCALAR_VALUE, 0);
                        value = createScalarValue(gmt, scalarSeed);
                    }
                    p.set(entity, value);
                }
            }
        }
    }

    private boolean isNullableScalar(GenericModelType gmt) {
        switch (gmt.getTypeCode()) {
            case decimalType:
            case stringType:
            case doubleType:
            case floatType:
            case dateType:
            case enumType:
            case integerType:
            case longType:
                return true;
            case booleanType:
            default:
                return false;
        }
    }

    private Object createScalarValue(GenericModelType type, long seed) {
        TypeCode tc = type.getTypeCode();
        long mix = Math.abs(hash64(seed)); // <-- fix: niemals negatives Vorzeichen

        switch (tc) {
        	case stringType:
        	    return "s-" + (mix % 1000);

        	case integerType:
        	    return (int) (mix % 1000);

        	case longType:
        	    return (mix % 100_000);

        	case floatType:
        	    return (float) ((mix % 1000) / 10.0);

        	case doubleType:
        	    return ((mix % 1000) / 10.0) * 1.37;

        	case decimalType:
        	    return BigDecimal.valueOf((mix % 1000) / 100.0);

        	case dateType:
        	    long days = (mix % 500) - 250;
        	    return new Date(1_700_000_000_000L + days * 24L * 3600L * 1000L);
        	
            case booleanType:
                return (mix & 1L) == 0L;

            case enumType:
                Enuma[] values = Enuma.values();
                return values[(int) Math.floorMod(mix, values.length)];

            default:
                return "x-" + mix;
        }
    }
    
    // ---------------- Relationen / Collections vernetzen ----------------

    private void linkRelations(Map<EntityType<?>, List<GenericEntity>> pool) {
        Map<GenericModelType, List<GenericEntity>> poolByModelType = new LinkedHashMap<>();
        for (Map.Entry<EntityType<?>, List<GenericEntity>> e : pool.entrySet()) {
            poolByModelType.put(e.getKey(), e.getValue());
        }

        for (Map.Entry<EntityType<?>, List<GenericEntity>> e : pool.entrySet()) {
            EntityType<?> type = e.getKey();
            List<GenericEntity> entities = e.getValue();
            String typeShort = typeShortNames.getOrDefault(type, type.getShortName());

            for (int entityIndex = 0; entityIndex < entities.size(); entityIndex++) {
                GenericEntity entity = entities.get(entityIndex);

                for (Property p : type.getProperties()) {
                    GenericModelType gmt = p.getType();
                    if (p.isIdentifying() || gmt.isScalar()) {
                        continue;
                    }

                    if (gmt.isEntity()) {
                        linkToOne(typeShort, entityIndex, entity, p, gmt, poolByModelType);
                    } else if (gmt.isCollection()) {
                        linkCollection(typeShort, entityIndex, entity, p, gmt, poolByModelType);
                    }
                }
            }
        }
    }

    private void linkToOne(String typeShort,
                           int entityIndex,
                           GenericEntity entity,
                           Property p,
                           GenericModelType gmt,
                           Map<GenericModelType, List<GenericEntity>> poolByModelType) {

        boolean assignNull = false;
        if (config.getNullRateToOne() > 0) {
            double rndNull = randomDouble(typeShort, p.getName(), entityIndex, ROLE_TO_ONE_NULL, 0);
            if (rndNull < config.getNullRateToOne()) {
                assignNull = true;
            }
        }

        if (assignNull) {
            p.set(entity, null);
            return;
        }

        List<GenericEntity> targetPool = poolByModelType.get(gmt);
        if (targetPool == null || targetPool.isEmpty()) {
            return;
        }

        long seed = scalarSeed(typeShort, p.getName(), entityIndex, ROLE_TO_ONE_PICK, 0);
        long mix = hash64(seed);
        int idx = (int) Math.floorMod(mix, targetPool.size());
        p.set(entity, targetPool.get(idx));
    }

    private void linkCollection(String typeShort,
                                int entityIndex,
                                GenericEntity entity,
                                Property p,
                                GenericModelType gmt,
                                Map<GenericModelType, List<GenericEntity>> poolByModelType) {

        CollectionType ct = (CollectionType) gmt;
        CollectionKind kind = ct.getCollectionKind();

        switch (kind) {
            case list:
            case set:
                linkLinearCollection(typeShort, entityIndex, entity, p, ct, poolByModelType);
                break;
            case map:
                linkMapCollection(typeShort, entityIndex, entity, p, (MapType) ct, poolByModelType);
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void linkLinearCollection(String typeShort,
                                      int entityIndex,
                                      GenericEntity entity,
                                      Property p,
                                      CollectionType ct,
                                      Map<GenericModelType, List<GenericEntity>> poolByModelType) {

        // empty collection?
        if (config.getEmptyRateCollection() > 0) {
            double rndEmpty = randomDouble(typeShort, p.getName(), entityIndex, ROLE_COLL_EMPTY, 0);
            if (rndEmpty < config.getEmptyRateCollection()) {
                p.set(entity, ct.createPlain());
                return;
            }
        }

        LinearCollectionType lct = (LinearCollectionType) ct;
        GenericModelType elemType = lct.getCollectionElementType();

        int size = elemType.isEntity()
                ? config.getEntityCollectionSize()
                : config.getScalarCollectionSize();

        Collection<Object> collection = lct.createPlain();

        if (elemType.isEntity()) {
            List<GenericEntity> targetPool = poolByModelType.get(elemType);
            if (targetPool != null && !targetPool.isEmpty()) {
                for (int i = 0; i < size; i++) {
                    long seed = scalarSeed(typeShort, p.getName(), entityIndex, ROLE_COLL_ENTITY_PICK, i);
                    long mix = hash64(seed);
                    int idx = (int) Math.floorMod(mix, targetPool.size());
                    collection.add(targetPool.get(idx));
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                long seed = scalarSeed(typeShort, p.getName(), entityIndex, ROLE_COLL_SCALAR_VALUE, i);
                Object value = createScalarValue(elemType, seed);
                collection.add(value);
            }
        }

        p.set(entity, collection);
    }

    @SuppressWarnings("unchecked")
    private void linkMapCollection(String typeShort,
                                   int entityIndex,
                                   GenericEntity entity,
                                   Property p,
                                   MapType mt,
                                   Map<GenericModelType, List<GenericEntity>> poolByModelType) {

        // empty map?
        if (config.getEmptyRateCollection() > 0) {
            double rndEmpty = randomDouble(typeShort, p.getName(), entityIndex, ROLE_COLL_EMPTY, 0);
            if (rndEmpty < config.getEmptyRateCollection()) {
                p.set(entity, mt.createPlain());
                return;
            }
        }

        GenericModelType keyType = mt.getKeyType();
        GenericModelType valueType = mt.getValueType();

        Map<Object, Object> map = mt.createPlain();

        Object lastKey = null;
        for (int i = 0; i < config.getMapSize(); i++) {

            // Kollision?
            boolean useLastKey = false;
            if (i > 0 && config.getMapCollisionRate() > 0) {
                double colRnd = randomDouble(typeShort, p.getName(), entityIndex, ROLE_MAP_COLLISION, i);
                if (colRnd < config.getMapCollisionRate()) {
                    useLastKey = true;
                }
            }

            Object key;
            if (useLastKey && lastKey != null) {
                key = lastKey;
            } else {
                long keySeed = scalarSeed(typeShort, p.getName(), entityIndex, ROLE_MAP_KEY, i);
                key = createValueForType(keyType, keySeed, poolByModelType);
                lastKey = key;
            }

            long valueSeed = scalarSeed(typeShort, p.getName(), entityIndex, ROLE_MAP_VALUE, i);
            Object value = createValueForType(valueType, valueSeed, poolByModelType);

            map.put(key, value);
        }

        p.set(entity, map);
    }

    private Object createValueForType(GenericModelType type,
                                      long seed,
                                      Map<GenericModelType, List<GenericEntity>> poolByModelType) {

        if (type.isEntity()) {
            List<GenericEntity> targetPool = poolByModelType.get(type);
            if (targetPool == null || targetPool.isEmpty()) {
                return null;
            }
            long mix = hash64(seed);
            int idx = (int) Math.floorMod(mix, targetPool.size());
            return targetPool.get(idx);
        }

        if (type.isScalar()) {
            return createScalarValue(type, seed);
        }

        return null;
    }

    // ---------------- Hash-/Random-Hilfen ----------------

    private long scalarSeed(String typeShort,
                            String propName,
                            int entityIndex,
                            int role,
                            int elementIndex) {

        long s = config.getSeed();
        s ^= hash32(typeShort != null ? typeShort.hashCode() : 0) * 0x9E3779B97F4A7C15L;
        s ^= hash32(propName != null ? propName.hashCode() : 0) * 0xBF58476D1CE4E5B9L;
        s ^= ((long) entityIndex + 1L) * 0x94D049BB133111EBL;
        s ^= ((long) role) * 0x632BE59BD9B4E019L;
        s ^= ((long) elementIndex + 1L) * 0x123456789ABCDEFL;
        return s;
    }

    private double randomDouble(String typeShort,
                                String propName,
                                int entityIndex,
                                int role,
                                int elementIndex) {

        long seed = scalarSeed(typeShort, propName, entityIndex, role, elementIndex);
        long rnd = hash64(seed);
        // in [0,1)
        long bits = (rnd >>> 11) & ((1L << 53) - 1);
        return bits / (double) (1L << 53);
    }

    private static long hash32(int x) {
        long z = x * 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        z ^= (z >>> 31);
        return z;
    }

    private static long hash64(long x) {
        x += 0x9E3779B97F4A7C15L;
        x = (x ^ (x >>> 30)) * 0xBF58476D1CE4E5B9L;
        x = (x ^ (x >>> 27)) * 0x94D049BB133111EBL;
        x ^= (x >>> 31);
        return x;
    }
}
