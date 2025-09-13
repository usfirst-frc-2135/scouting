package com.frc2135.android.frc_scout;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class AliasesInfo
{
    private static final String TAG = "AliasesInfo";

    // Data members
    private String m_eventCode;
    private JSONArray m_jsonData;
    private boolean m_bAliasesDataLoaded;

    private static AliasesInfo sAliasesInfo;

    private AliasesInfo(String eventCode)
    {
        m_eventCode = eventCode;
        m_bAliasesDataLoaded = false;
        m_jsonData = null;
    }

    public static AliasesInfo get(Context context, String eventCode, boolean bForceReload)
    {
        if (sAliasesInfo != null)
        {
            String oldEventCode = sAliasesInfo.getEventCode();
            if (bForceReload || !oldEventCode.equals(eventCode))
            {
                sAliasesInfo.setEventCode(eventCode);
                Log.d(TAG, "Resetting existing " + oldEventCode + " sAliasesInfo for new eventCode " + eventCode);
                sAliasesInfo.readAliasesJSON(context, true);
            }
        }
        else
        {
            Log.d(TAG, "Creating a new sAliasesInfo for eventCode " + eventCode);
            sAliasesInfo = new AliasesInfo(eventCode);
            // Read in matches json file if there is one.
            sAliasesInfo.readAliasesJSON(context, true);
        }
        return sAliasesInfo;
    }

    public static void clear()
    {
        if (sAliasesInfo != null)
        {
            Log.d(TAG, "Deleting existing sAliasesInfo");
            sAliasesInfo = null;
        }
        else
            Log.d(TAG, "No action needed: no existing sAliasesInfo");
    }

    public String getEventCode()
    {
        return m_eventCode;
    }

    // Get the alias ("99" number) for the given teamnum (B/C/D num)
    public String getAliasForTeamNum(String teamNumStr) throws JSONException
    {
        String rtnVal = "";
        if (m_jsonData != null)
        {
            // Strip "frc" prefix from given teamNumStr if needed.
            teamNumStr = MatchData.stripTeamNumPrefix(teamNumStr);

            // Go thru the array of aliases data and find the one for the given teamNumStr.
            for (int ctr = 0; ctr < m_jsonData.length(); ctr++) 
            {
                JSONObject data1 =(JSONObject) m_jsonData.get(ctr);
                String num = data1.getString("teamnum");
//                Log.d(TAG, "For jsonData[" + ctr + "], teamnum = " + num);
                if (num.equals(teamNumStr))
                {
                    rtnVal = data1.getString("aliasnum");
                    Log.d(TAG, "Found alias for teamnum " + num + ": " + rtnVal);
                    break;
                }
            }
        }
        return rtnVal;
    }

    // Get the teamnum (B/C/D num) for the given alias ("99" number) 
    public String getTeamNumForAlias(String myAlias) throws JSONException
    {
        String rtnVal = "";
        if (m_jsonData != null)
        {
            // Go thru the array of aliases data and find the one for the given aliasNum.
            for (int ctr = 0; ctr < m_jsonData.length(); ctr++)
            {
                JSONObject data1 =(JSONObject) m_jsonData.get(ctr);
                String aliasnum = data1.getString("aliasnum");
                Log.d(TAG, "For jsonData[" + ctr + "], aliasnum = " + aliasnum);
                if (aliasnum.equals(myAlias))
                {
                    rtnVal = data1.getString("teamnum");
                    Log.d(TAG, "Found teamnum for alias " + aliasnum + ": " + rtnVal);
                    break;
                }
            }
        }
        return rtnVal;
    }


    public void setEventCode(String eventCode)
    {
        m_eventCode = eventCode;
        m_bAliasesDataLoaded = false;
        m_jsonData = null;
    }

    public void readAliasesJSON(Context context, boolean bSilent)
    {
        String filename = context.getFilesDir().getPath() + "/" + m_eventCode.trim().toLowerCase() + "_aliases.json";
        Log.d(TAG, "Looking for aliases JSON file: " + filename);
        File file = new File(filename);

        // Check if file exists before trying to read it.
        if (file.exists())
        {
            Log.d(TAG, "Attempting to read in aliases JSON file");
            BufferedReader reader;
            try
            {
                // Open and read the file into a StringBuilder.
                InputStream in = context.openFileInput(file.getName().trim());
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    jsonString.append(line);
                }

                // Parse the JSON.
                m_jsonData = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                Log.d(TAG, "setting m_bAliasesDataLoaded = true");
                m_bAliasesDataLoaded = true;

                // Show success Toast msg 
                String msg = "Successfully read aliases file from device: " + m_eventCode + " _aliases.json file ";
                Log.d(TAG, msg);
                Toast toastS = Toast.makeText(context, msg, Toast.LENGTH_LONG);
//REMOVE                toastS.setGravity(Gravity.CENTER, 0, 0);
                toastS.show();
                reader.close();
            } catch (FileNotFoundException err)
            {
                if (!bSilent)
                {
                    // Show error Toast msg 
                    String errMsg = "ERROR reading aliases file from device: \n" + err;
                    Log.e(TAG, errMsg);
                    Toast toastM = Toast.makeText(context, errMsg, Toast.LENGTH_LONG);
 //REMOVE                   View view2 = toastM.getView();
 //REMOVE                   Objects.requireNonNull(view2).setBackgroundColor(Color.RED);
 //REMOVE                   toastM.setGravity(Gravity.CENTER, 0, 0);
                    toastM.show();
                }
            } catch (JSONException jsonException)
            {
                Log.e(TAG, "ERROR (jsonException) reading aliases file\n");
                jsonException.printStackTrace();
            } catch (IOException ioException)
            {
                Log.e(TAG, "ERROR (ioException) reading aliases file\n");
                ioException.printStackTrace();
            }
        } else Log.d(TAG, "File doesn't exist: " + filename);
    }

    public boolean isAliasesDataLoaded()
    {
        Log.d(TAG, "isAliasesDataLoaded() returning " + m_bAliasesDataLoaded);
        return m_bAliasesDataLoaded;
    }

    /*public String[] getTeams(String matchNum) throws JSONException
    {
        String[] teams = new String[7];
        teams[0] = "";   // initialize with empty strings
        teams[1] = "";
        teams[2] = "";
        teams[3] = "";
        teams[4] = "";
        teams[5] = "";
        teams[6] = "";
        boolean bMatchNumFound = false;
        if (m_jsonData != null)
        {
            JSONObject tempB;
            JSONObject tempR;
            JSONArray redTeams = new JSONArray();
            JSONArray blueTeams = new JSONArray();
            for (int i = 0; i < m_jsonData.length(); i++)
            {
                if ((((JSONObject) m_jsonData.get(i)).getString("comp_level") + ((JSONObject) m_jsonData.get(i)).getString("match_number")).equals(matchNum.trim().toLowerCase()))
                {
                    bMatchNumFound = true;
                    JSONObject alliances = (JSONObject) m_jsonData.get(i);
                    JSONObject color = (JSONObject) alliances.get("alliances");
                    tempB = (JSONObject) color.get("blue");
                    blueTeams = (JSONArray) tempB.get("team_keys");
                    tempR = (JSONObject) color.get("red");
                    redTeams = (JSONArray) tempR.get("team_keys");
                    break;
                }
            }
            if (bMatchNumFound)
            {
                teams[0] = "No team selected";
                for (int i = 1; i < 4; i++)
                {
                    teams[i] = redTeams.getString(i - 1);
                }
                for (int i = 4; i < 7; i++)
                {
                    teams[i] = blueTeams.getString(i - 4);
                }
            }
            else
                Log.d(TAG, "getTeams(): matchNum '" + matchNum + "' NOT found!");
        }
        return teams;
  }

     */
}
