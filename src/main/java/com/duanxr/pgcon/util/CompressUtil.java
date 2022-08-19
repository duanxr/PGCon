package com.duanxr.pgcon.util;

import lombok.experimental.UtilityClass;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * @author 段然 2022/8/13
 */
@UtilityClass
public class CompressUtil {
  private static final LZ4Factory LZ_4_FACTORY = LZ4Factory.fastestJavaInstance();
  private static final LZ4Compressor LZ_4_COMPRESSOR = LZ_4_FACTORY.highCompressor();
  private static final LZ4FastDecompressor LZ_4_DECOMPRESSOR = LZ_4_FACTORY.fastDecompressor();

  public byte[] lz4Compress(byte[] bytes) {
    synchronized (LZ_4_COMPRESSOR) {
      return LZ_4_COMPRESSOR.compress(bytes);
    }
  }
  public byte[] lz4Decompress(byte[] bytes, int length) {
    synchronized (LZ_4_DECOMPRESSOR) {
      return LZ_4_DECOMPRESSOR.decompress(bytes, length);
    }
  }


}
