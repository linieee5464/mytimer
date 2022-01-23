package com.example.mytimer;

import java.util.HashMap;
import java.util.Map;
/*Helper class to hold mapping between exercise ID on FireBase and image name in local app*/
public class ExerciseInfo {
    private String name = "";
    private String length = "";
    private String suggestion = "";
    private Map<String, Integer> idImageMapping = new HashMap<String, Integer>();

    public ExerciseInfo(String type){
        name = type;
        suggestion = "Best to do 3-5 sets";
        if(type.equals("cardio")){
            length = "45 seconds / set | 15 seconds / rest";
            idImageMapping.put("climber", R.drawable.climber);
            idImageMapping.put("jumpingjack", R.drawable.jumpingjack);
            idImageMapping.put("butt_kick", R.drawable.butt_kick);
            idImageMapping.put("burpees", R.drawable.burpees);
            idImageMapping.put("high_knee", R.drawable.high_knee);
            idImageMapping.put("plank_rotation", R.drawable.plank_rotation);
            idImageMapping.put("jump_squat", R.drawable.jump_squat);
            idImageMapping.put("reveres_lunge", R.drawable.reverse_lunge);
        }else{
            length = "1 minute / set | 15s / rest";
            idImageMapping.put("knee_push_up", R.drawable.knee_push_up);
            idImageMapping.put("plank_up_down", R.drawable.plank_up_down);
            idImageMapping.put("bicep_curl", R.drawable.bicep_curl);
            idImageMapping.put("tricep_dips", R.drawable.tricep_dips);
            idImageMapping.put("shoulder_press", R.drawable.shoulder_press);
            idImageMapping.put("crunches", R.drawable.crunches);
            idImageMapping.put("flutter_kicks", R.drawable.flutter_kicks);
            idImageMapping.put("heel_tap", R.drawable.heel_tap);
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public void setLength(String length){
        this.length = length;
    }

    public String getName(){
        return name.toLowerCase();
    }

    public String getLength(){
        return length;
    }

    public String getSuggestion(){
        return suggestion;
    }

    public Map<String, Integer> getIDImage(){
        return idImageMapping;
    }
}
