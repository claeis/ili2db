package ch.ehi.ili2db.toxtf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.ehi.iox.objpool.impl.AbstractIomObjectSerializer;
import ch.ehi.iox.objpool.impl.Serializer;
import ch.interlis.iom.IomObject;

public class FixIomObjectRefsSerializer extends AbstractIomObjectSerializer implements Serializer<FixIomObjectRefs> {

    @Override
    public FixIomObjectRefs getObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in=new ByteArrayInputStream(bytes);
        startObject();
        IomObject root=readIomObject(in);
        int refc=readInt(in);
        FixIomObjectRefs ret=new FixIomObjectRefs();
        ret.setRoot(root);
        for(int refi=0;refi<refc;refi++) {
            IomObject ref=readIomObject(in);
            String targetClass=mapIdx2Name(readInt(in));
            long targetSqlId=readLong(in);
            String targetSqlTable=mapIdx2Name(readInt(in));
            ret.addFix(ref, targetSqlId, targetClass, targetSqlTable);
        }
        endObject();
        return ret;
    }

    @Override
    public byte[] getBytes(FixIomObjectRefs object) throws IOException {
        ByteArrayOutputStream  byteStream = new ByteArrayOutputStream();
        startObject();
        writeIomObject(byteStream, object.getRoot());
        int refc=object.getRefsCount();
        writeInt(byteStream,refc);
        for(IomObject ref:object.getRefs()) {
            writeIomObject(byteStream, ref);
            writeInt(byteStream,mapName2Idx(object.getTargetClass(ref)));
            writeLong(byteStream,object.getTargetSqlid(ref));
            writeInt(byteStream,mapName2Idx(object.getTargetSqlTable(ref)));
        }
        endObject();
        return byteStream.toByteArray();
    }

}
