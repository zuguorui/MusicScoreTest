package com.zu.musicscoretest.music_score;

import com.zu.musicxml3_0.ScorePartwise;
import com.zu.musicxml3_0.ScoreTimewise;
import com.zu.musicxml3_0.Work;
import com.zu.musicxml3_0.opus.Opus;

import java.io.InputStream;

public class MusicScore {

    public static float LINE_SPACE = 6.0f;
    public static float TRACK_SPACE = 40.0f;


    public static float DPI = 300; // 印刷行业标准

    // 毫米，默认为A4标准尺寸，竖向。
    public static float DEFAULT_PAGE_WIDTH_MM = 210;
    public static float DEFAULT_PAGE_HEIGHT_MM = 297;

    public static float DEFAULT_V_MARGIN = 15;
    public static float DEFAULT_H_MARGIN = 15;

    private Float pageWidth = null;
    private Float pageHeight = null;


    public enum Type{
        PARTWISE,
        TIMEWISE,
        OPUS,
        UNKNOWN
    }

    private Type type = Type.UNKNOWN;

    private Object score = null;



    public MusicScore()
    {

    }

    public boolean load(InputStream is)
    {
        boolean result = true;
        try{
            score = Unmarshall.unmarshall(is);
            if(score instanceof ScorePartwise)
            {
                type = Type.PARTWISE;
                result = analysePartwise();
            }else if(score instanceof ScoreTimewise)
            {
                type = Type.TIMEWISE;
                result = analyseTimewise();
            }else if(score instanceof Opus)
            {
                type = Type.OPUS;
            }else
            {
                type = Type.UNKNOWN;
                result = false;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            result = false;
        }
        return result;

    }

    private boolean analysePartwise()
    {
        if(score == null || !(score instanceof ScorePartwise))
        {
            return false;
        }
        ScorePartwise scoreP = (ScorePartwise)score;
        boolean result = true;
        if(scoreP.getWork() != null)
        {
            analyseWork(scoreP.getWork());
        }
        return result;
    }

    private boolean analyseTimewise()
    {
        if(score == null || !(score instanceof ScoreTimewise))
        {
            return false;
        }
        ScoreTimewise scoreT = (ScoreTimewise)score;
        boolean result = true;

        if(scoreT.getWork() != null)
        {
            analyseWork(scoreT.getWork());
        }

        return result;
    }

    private void analyseWork(Work work)
    {
        if(work == null)
        {
            return;
        }

    }


}
