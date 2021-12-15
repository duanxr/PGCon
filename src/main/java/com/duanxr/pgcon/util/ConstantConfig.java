package com.duanxr.pgcon.util;

import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Duanran 2019/12/17
 */
public class ConstantConfig {

  public static final String MAIN_PANEL_TITLE = "PGCon - by duanxr(github)";

  public static final int MAIN_PANEL_VIDEO_FRAME_DELAY = 25;

  public static final int MAIN_PANEL_VIDEO_RETRY_DELAY = 1000;

  public static final int OUTPUT_SERIAL_BAUD_RATE = 9600;

  public static final int OUTPUT_PRESS_TIME = 150;

  public static final int OUTPUT_INTERVAL = 150;

  public static final int OUTPUT_PRESS_CHECK_INTERVAL = 10;

  public static final int SCRIPT_IMAGE_CHECK_INTERVAL = 10;

  public static final int TEMPLATE_MATCH_METHOD = Imgproc.TM_CCORR_NORMED;

  public static final int TEMPLATE_MATCH_THREADS = 600;

  public static final double TEMPLATE_MATCH_THRESHOLD = 0.95d;

  public static final int TEMPLATE_MATCH_RECT_SHOW_TIME = 3000;

  public static final int MULTIPLE_TEMPLATE_MATCH_TIMEOUT = 1000;

  public static final int MULTIPLE_OCR_TIMEOUT = 1000;

  public static final int NINTENDO_SWITCH_PPI = 237;

  public static final Size SIZE = new Size(1280, 720);

  public static final int INPUT_VIDEO_FRAME_INTERVAL = 10;

  public static final int INPUT_VIDEO_FRAME_CACHE_SIZE = 20;
}
