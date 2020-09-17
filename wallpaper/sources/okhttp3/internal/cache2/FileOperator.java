package okhttp3.internal.cache2;

import java.io.IOException;
import java.nio.channels.FileChannel;
import okio.Buffer;

final class FileOperator {
    private final FileChannel fileChannel;

    FileOperator(FileChannel fileChannel2) {
        this.fileChannel = fileChannel2;
    }

    public void write(long pos, Buffer source, long byteCount) throws IOException {
        if (byteCount < 0 || byteCount > source.size()) {
            throw new IndexOutOfBoundsException();
        }
        long byteCount2 = pos;
        long byteCount3 = byteCount;
        while (byteCount3 > 0) {
            long bytesWritten = this.fileChannel.transferFrom(source, byteCount2, byteCount3);
            byteCount3 -= bytesWritten;
            byteCount2 += bytesWritten;
        }
    }

    public void read(long byteCount, Buffer sink, long byteCount2) throws IOException {
        if (byteCount2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        while (byteCount2 > 0) {
            long bytesRead = this.fileChannel.transferTo(byteCount, byteCount2, sink);
            byteCount2 -= bytesRead;
            byteCount += bytesRead;
        }
    }
}
