package io.github.yuazer.zpokestoresql.utils;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import java.io.*;

public class NBTUtils {
    public static byte[] serializeNBT(NBTTagCompound nbt) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
        CompressedStreamTools.func_74800_a(nbt, dataOutput);
        return byteArrayOutputStream.toByteArray();
    }

    public static NBTTagCompound deserializeNBT(byte[] data) throws IOException {
        return CompressedStreamTools.func_152456_a(new DataInputStream(new ByteArrayInputStream(data)), NBTSizeTracker.field_152451_a);
    }
}
