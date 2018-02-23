package org.jackframework.common;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CharsWriter extends Writer {

    protected char[] buffer;

    protected int size;

    public CharsWriter() {
        buffer = CaptainTools.mallocBuffer();
    }

    public CharsWriter(int minCapacity) {
        buffer = CaptainTools.mallocBuffer(minCapacity);
    }

    @Override
    public void write(int c) {
        int minCapacity = size + 1;
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }
        buffer[size] = (char) c;
        size = minCapacity;
    }

    @Override
    public void write(char[] cbuf) {
        if (cbuf == null) {
            return;
        }
        write(cbuf, 0, cbuf.length);
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        int minCapacity = size + len;
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }
        System.arraycopy(cbuf, off, buffer, size, len);
        size = minCapacity;
    }

    @Override
    public void write(String str) {
        if (str == null) {
            str = "null";
        }
        write(str, 0, str.length());
    }

    @Override
    public void write(String str, int off, int len) {
        int minCapacity = size + len;
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }
        str.getChars(off, off + len, buffer, size);
        size = minCapacity;
    }

    public void writeInt(int value) {
        int minCapacity = size + CaptainTools.stringSize(value);
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }
        CaptainTools.getChars(value, buffer, size = minCapacity);
    }

    public void write(long value) {
        int minCapacity = size + CaptainTools.stringSize(value);
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }

        CaptainTools.getChars(value, buffer, size = minCapacity);
    }

    public void write(Reader reader) throws IOException {
        write(reader, CaptainTools.DEFAULT_THREAD_CHAR_BUFFER_SIZE);
    }

    public void write(Reader reader, int bufferSize) throws IOException {
        if (reader == null) {
            return;
        }
        int minCapacity = size + bufferSize, len;
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }
        while ((len = reader.read(buffer, size, bufferSize)) != -1) {
            size += len;
            minCapacity = size + bufferSize;
            if (minCapacity > buffer.length) {
                buffer = CaptainTools.growCapacity(buffer, minCapacity);
            }
        }
    }

    public void write(InputStream in) throws IOException {
        write(in, CaptainTools.CHARSET_UTF8, CaptainTools.DEFAULT_THREAD_CHAR_BUFFER_SIZE);
    }

    public void write(InputStream in, int bufferSize) throws IOException {
        write(in, CaptainTools.CHARSET_UTF8, bufferSize);
    }

    public void write(InputStream in, String charsetName, int bufferSize) throws IOException {
        write(new InputStreamReader(in, charsetName), bufferSize);
    }

    public void write(InputStream in, Charset charset, int bufferSize) throws IOException {
        if (in == null) {
            return;
        }
        write(new InputStreamReader(in, charset), bufferSize);
    }

    public void write(InputStream in, CharsetDecoder decoder, int bufferSize) throws IOException {
        if (in == null) {
            return;
        }
        write(new InputStreamReader(in, decoder), bufferSize);
    }

    public void write(Object object) {
        write(String.valueOf(object));
    }

    public void writeSkipNull(Object object) {
        if (object == null) {
            return;
        }
        write(object.toString());
    }

    public void writeSubstring(String str, int start, int end) {
        int minCapacity = size + (end - start);
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }
        str.getChars(start, end, buffer, size);
        size = minCapacity;
    }

    public void writeSubstring(char[] cbuf, int start, int end) {
        int length = end - start;
        int minCapacity = size + length;
        if (minCapacity > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, minCapacity);
        }
        System.arraycopy(cbuf, start, buffer, size, length);
        size = minCapacity;
    }

    public CharsWriter append(int value) {
        writeInt(value);
        return this;
    }

    public CharsWriter append(char[] cbuf) {
        write(cbuf);
        return this;
    }

    public CharsWriter append(char[] cbuf, int off, int len) {
        write(cbuf, off, len);
        return this;
    }

    public CharsWriter append(String str) {
        write(str);
        return this;
    }

    public CharsWriter append(String str, int off, int len) {
        write(str, off, len);
        return this;
    }

    public CharsWriter append(long value) {
        write(value);
        return this;
    }

    public CharsWriter append(Reader reader) throws IOException {
        write(reader);
        return this;
    }

    public CharsWriter append(Reader reader, int bufferSize) throws IOException {
        write(reader, bufferSize);
        return this;
    }

    public CharsWriter append(InputStream in) throws IOException {
        write(in);
        return this;
    }

    public CharsWriter append(InputStream in, int bufferSize) throws IOException {
        write(in, bufferSize);
        return this;
    }

    public CharsWriter append(InputStream in, String charsetName, int bufferSize) throws IOException {
        write(in, charsetName, bufferSize);
        return this;
    }

    public CharsWriter append(InputStream in, Charset charset, int bufferSize) throws IOException {
        write(in, charset, bufferSize);
        return this;
    }

    public CharsWriter append(InputStream in, CharsetDecoder decoder, int bufferSize) throws IOException {
        write(in, decoder, bufferSize);
        return this;
    }

    public CharsWriter append(Object object) {
        write(object);
        return this;
    }

    public CharsWriter appendSkipNull(Object obj) {
        writeSkipNull(obj);
        return this;
    }

    public CharsWriter appendSubstring(String str, int start, int end) {
        writeSubstring(str, start, end);
        return this;
    }

    public CharsWriter appendSubstring(char[] cbuf, int start, int end) {
        writeSubstring(cbuf, start, end);
        return this;
    }

    @Override
    public CharsWriter append(CharSequence csq) {
        write(String.valueOf(csq));
        return this;
    }

    @Override
    public CharsWriter append(CharSequence csq, int start, int end) {
        write(csq == null ? "null" : csq.subSequence(start, end).toString());
        return this;
    }

    @Override
    public CharsWriter append(char c) {
        write(c);
        return this;
    }

    public void reset() {
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 0) {
            throw new OutOfMemoryError("Size must be greater than or equal to 0.");
        }
        if (size > buffer.length) {
            buffer = CaptainTools.growCapacity(buffer, size);
        }
        this.size = size;
    }

    public char[] getBuffer() {
        return buffer;
    }

    @Override
    public String toString() {
        return new String(buffer, 0, size);
    }

    public String closeToString() {
        return closeThen().toString();
    }

    @Override
    public void flush() throws IOException {
        // Do nothing.
    }

    @Override
    public void close() {
        CaptainTools.recycleBuffer(buffer);
    }

    public CharsWriter closeThen() {
        close();
        return this;
    }

    public CharsWriter resetThen() {
        reset();
        return this;
    }

    public CharsWriter setSizeThen(int size) {
        setSize(size);
        return this;
    }

}
