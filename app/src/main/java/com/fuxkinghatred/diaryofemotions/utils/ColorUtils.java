package com.fuxkinghatred.diaryofemotions.utils;

import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

/**
 * Работа с цветом.
 */
public class ColorUtils {

    /**
     * Преобразует цвет из формата RGB в формат HSL.
     *
     * @param red   компонент красного цвета в диапазоне 0-255
     * @param green компонент зеленого цвета в диапазоне 0-255
     * @param blue  компонент синего цвета в диапазоне 0-255
     * @return массив из трех элементов, представляющих цвет в формате HSL (Hue, Saturation, Lightness)
     */
    public static float[] rgbToHsl(int red, int green, int blue) {
        // Создаем матрицу размером 1x1 для одного пикселя в формате BGR
        Mat rgbMat = new Mat(1, 1, CvType.CV_8UC3);
        // Заполняем матрицу значениями BGR (порядок цветов в OpenCV)
        rgbMat.put(0, 0, new byte[]{(byte) blue, (byte) green, (byte) red});

        // Создаем пустую матрицу для результата преобразования в HLS
        Mat hslMat = new Mat();

        // Преобразуем цвет из RGB в HLS (HLS в OpenCV соответствует HSL)
        Imgproc.cvtColor(rgbMat, hslMat, Imgproc.COLOR_RGB2HLS);

        // Извлекаем значения HLS из матрицы
        double[] hslValues = hslMat.get(0, 0);

        // Приводим значения к нужному формату:
        // - Hue: диапазон 0-360
        // - Saturation: диапазон 0-100
        // - Lightness: диапазон 0-100
        float hue = (float) hslValues[0] * 2;
        float saturation = (float) hslValues[2] / 255f * 100f;
        float lightness = (float) hslValues[1] / 255f * 100f;

        // Возвращаем результат в виде массива
        return new float[]{hue, saturation, lightness};
    }
}