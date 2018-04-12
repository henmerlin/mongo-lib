/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.net.gvt.efika.mongo.dao;

import br.net.gvt.efika.mongo.dao.converter.BigIntegerConverter;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.UpdateOperations;

/**
 *
 * @author G0042204
 * @param <T>
 */
public abstract class AbstractMongoDAO<T> implements GenericDAO<T> {

    private static Datastore datastore;
    private final String ipAddress;
    private final String dbName;
    private final Class<T> typeParameterClass;
    private Morphia morphia;

    public AbstractMongoDAO(String ipAddress, String dbName, Class<T> typeParameterClass) {
        this.ipAddress = ipAddress;
        this.dbName = dbName;
        this.typeParameterClass = typeParameterClass;
    }

    public Datastore getDatastore() {
        if (morphia == null) {
            morphia = new Morphia();
            morphia.getMapper().getConverters().addConverter(BigIntegerConverter.class);
        }
        datastore = morphia.createDatastore(new MongoClient(ipAddress), dbName);
        return datastore;
    }

    @Override
    public UpdateOperations<T> createUpdateOperations() {
        UpdateOperations<T> u = getDatastore().createUpdateOperations(typeParameterClass);
        getDatastore().getMongo().close();
        return u;
    }

    @Override
    public T save(T t) throws Exception {
        getDatastore().save(t);
        getDatastore().getMongo().close();
        return t;
    }

    @Override
    public T update(T t, UpdateOperations<T> opers) throws Exception {
        T res = (T) getDatastore().update(t, opers);
        getDatastore().getMongo().close();
        return res;
    }

    @Override
    public void delete(T t) throws Exception {
        getDatastore().delete(t);
        getDatastore().getMongo().close();
    }

    @Override
    public T read(ObjectId id) throws Exception {
        T res = getDatastore().get(typeParameterClass, id);
        getDatastore().getMongo().close();
        return res;
    }

    public Class<T> getTypeParameterClass() {
        return typeParameterClass;
    }

}
