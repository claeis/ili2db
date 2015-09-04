package ch.interlis.iox_j.wkb;

public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {
	
	private java.nio.ByteBuffer buf=null;

	public ByteArrayOutputStream() {
		super();
		buf=java.nio.ByteBuffer.allocate(8);
		buf.order(java.nio.ByteOrder.nativeOrder());
	}
	public ByteArrayOutputStream(java.nio.ByteOrder byteOrder) {
		super();
		buf=java.nio.ByteBuffer.allocate(8);
		buf.order(byteOrder);
	}
	public java.nio.ByteOrder order()
	{
		return buf.order();
	}
	public void writeInt(int value)
	{
		buf.rewind();
		buf.putInt(value);
		write(buf.array(),0,buf.position());
	}
	public void writeDouble(double value)
	{
		buf.rewind();
		buf.putDouble(value);
		write(buf.array(),0,buf.position());
	}

}
