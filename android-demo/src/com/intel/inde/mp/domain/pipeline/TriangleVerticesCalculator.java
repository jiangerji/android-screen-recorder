package com.intel.inde.mp.domain.pipeline;

import java.util.Arrays;
import java.util.Comparator;

import com.intel.inde.mp.domain.Pair;

public class TriangleVerticesCalculator
{
    static int stride = 5;
    private final float[] triangleVerticesData;
    private final float[] defaultTriangleVerticesData;
    private final float[] scale = { 1.0F, 1.0F };

    public TriangleVerticesCalculator() {
        this.defaultTriangleVerticesData = getDefaultTriangleVerticesData();
        this.triangleVerticesData = getDefaultTriangleVerticesData();
    }

    public TriangleVerticesCalculator(float[] triangleVerticesData) {
        this.defaultTriangleVerticesData = triangleVerticesData;
        this.triangleVerticesData = triangleVerticesData;
    }

    public float[] getScale(
            int angle, int widthIn, int heightIn, int widthOut, int heightOut)
    {
        float tmp11_10 = 1.0F;
        this.scale[1] = tmp11_10;
        this.scale[0] = tmp11_10;
        if ((angle == 90) || (angle == 270)) {
            int cx = widthIn;
            widthIn = heightIn;
            heightIn = cx;
        }

        float aspectRatioIn = widthIn / heightIn;
        float heightOutCalculated = widthOut / aspectRatioIn;

        if (heightOutCalculated < heightOut)
            this.scale[1] = (heightOutCalculated / heightOut);
        else {
            this.scale[0] = (heightOut * aspectRatioIn / widthOut);
        }

        return this.scale;
    }

    public float[] getAspectRatioVerticesData(
            int widthIn, int heightIn, int widthOut, int heightOut) {
        System.arraycopy(this.defaultTriangleVerticesData,
                0,
                this.triangleVerticesData,
                0,
                this.defaultTriangleVerticesData.length);

        float aspectRatioIn = widthIn / heightIn;
        float heightOutCalculated = widthOut / aspectRatioIn;

        if (heightOutCalculated < heightOut) {
            float deltaHeight = (heightOut - heightOutCalculated) / heightOut;
            GetMinMax vertical = new GetMinMax(1).invoke();
            this.triangleVerticesData[vertical.getMin1()] += deltaHeight;
            this.triangleVerticesData[vertical.getMin2()] += deltaHeight;
            this.triangleVerticesData[vertical.getMax1()] -= deltaHeight;
            this.triangleVerticesData[vertical.getMax2()] -= deltaHeight;
        } else {
            float deltaWidth = (widthOut - heightOut * aspectRatioIn)
                    / widthOut;
            GetMinMax horizontal = new GetMinMax(0).invoke();
            this.triangleVerticesData[horizontal.getMin1()] += deltaWidth;
            this.triangleVerticesData[horizontal.getMin2()] += deltaWidth;
            this.triangleVerticesData[horizontal.getMax1()] -= deltaWidth;
            this.triangleVerticesData[horizontal.getMax2()] -= deltaWidth;
        }

        return this.triangleVerticesData;
    }

    public static float[] getDefaultTriangleVerticesData() {
        return new float[] { -1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F,
                1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F,
                1.0F, 1.0F };
    }

    private class GetMinMax
    {
        private Pair<Integer, Float>[] array = new Pair[4];
        private int offset;

        GetMinMax(int offset)
        {
            this.offset = offset;
        }

        private int getOutIdx(int internalIdx) {
            return ((Integer) this.array[internalIdx].left).intValue()
                    * TriangleVerticesCalculator.stride + this.offset;
        }

        public int getMax1() {
            return getOutIdx(2);
        }

        public int getMax2() {
            return getOutIdx(3);
        }

        public int getMin1() {
            return getOutIdx(0);
        }

        public int getMin2() {
            return getOutIdx(1);
        }

        public GetMinMax invoke()
        {
            for (int i = 0; i < 4; i++) {
                this.array[i] = new Pair(Integer.valueOf(i),
                        Float.valueOf(TriangleVerticesCalculator.this.triangleVerticesData[(this.offset + TriangleVerticesCalculator.stride
                                * i)]));
            }
            Arrays.sort(this.array, new Comparator<Pair<Integer, Float>>()
            {
                @Override
                public int compare(
                        Pair<Integer, Float> o1, Pair<Integer, Float> o2) {
                    return Float.compare(((Float) o1.right).floatValue(),
                            ((Float) o2.right).floatValue());
                }
            });
            return this;
        }
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.domain.pipeline.TriangleVerticesCalculator
 * JD-Core Version: 0.6.1
 */
