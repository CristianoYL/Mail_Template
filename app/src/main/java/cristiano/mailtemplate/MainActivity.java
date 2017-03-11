package cristiano.mailtemplate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import cristiano.mailtemplate.adapter.TemplateListAdapter;
import cristiano.mailtemplate.database.DatabaseHelper;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseHelper myDB;

    private final static String[] DEVELOPER_EMAIL = {"li.yin.cristiano@rutgers.edu", "cw700@scarletmail.rutgers.edu"};
    public static final String ENTER_TAG = "[$ENTER$]";
    public static final String SPLIT_TAG = "<SPLIT>";

    private int currentPage = -1;
    private static final int TEMPLATE_PAGE = 0;
    private static final int LOAD_PAGE = 1;
    private static final int ABOUT_US_PAGE = 2;
//    private static final int FEED_BACK_PAGE = 3;
    private boolean isFirstRun = true;
    private boolean isDeleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Branch.getAutoInstance(getApplicationContext());
        myDB = new DatabaseHelper(getApplicationContext());


        try {
            currentPage = savedInstanceState.getInt("CURRENT_PAGE");
            isFirstRun = savedInstanceState.getBoolean("IS_FIRSTRUN");
        } catch (NullPointerException e) {
        }
        if(isFirstRun){
            initialSampleTemplate();
            isFirstRun = false;
        }
        switch (currentPage){
            case TEMPLATE_PAGE:
                showTemplatePage(null);
                break;
            default:
                showLoadPage();
                break;
        }

    }

    @Override
   public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt("CURRENT_PAGE",currentPage);
        savedInstanceState.putBoolean("IS_FIRSTRUN",isFirstRun);
        super.onSaveInstanceState(savedInstanceState);
   }



    @Override
    public void onStart() {
        super.onStart();

        Branch branch = Branch.getInstance(getApplicationContext());
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentPage == TEMPLATE_PAGE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("Do you want to exit Mail Template?\nAll unsaved changes will be discarded.");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();

                }
            });
            builder.setCancelable(true);
            builder.show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Warning");
            builder.setMessage("Do you want to exit Mail Template?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();

                }
            });
            builder.setCancelable(true);
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch (this.currentPage) {
            case TEMPLATE_PAGE:
                getMenuInflater().inflate(R.menu.menu_template, menu);
                break;
            case LOAD_PAGE:
                getMenuInflater().inflate(R.menu.menu_load, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.menu_default, menu);
                break;
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_load) {
            if(currentPage == TEMPLATE_PAGE){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Do you want to leave?\nAll unsaved changes will be discarded.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadPage();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setCancelable(true);
                builder.show();
            } else {
                showLoadPage();
            }
            return true;
        }
        if (id == R.id.action_delete) {
            this.isDeleteMode = true;
            Toast.makeText(MainActivity.this,"Please select the template you want to delete.",Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_create) {
            showTemplatePage(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create) {
            if(currentPage == TEMPLATE_PAGE){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Do you want to create a new template?\nAll unsaved changes will be discarded.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showTemplatePage(null);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setCancelable(true);
                builder.show();
            } else {
                showTemplatePage(null);
            }
        } else if (id == R.id.nav_load) {
            if(currentPage == TEMPLATE_PAGE){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Do you want to leave?\nAll unsaved changes will be discarded.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadPage();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setCancelable(true);
                builder.show();
            } else {
                showLoadPage();
            }
        } else if (id == R.id.nav_about_us) {
            if(currentPage == TEMPLATE_PAGE){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Do you want to leave?\nAll unsaved changes will be discarded.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAboutUs();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setCancelable(true);
                builder.show();
            } else {
                showAboutUs();
            }
        } else if (id == R.id.nav_feed_back) {
            sendMail(DEVELOPER_EMAIL,"Feedback",null);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAboutUs() {
        currentPage = ABOUT_US_PAGE;
        setContentView(R.layout.activity_about_us);
        displayMenus();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //                    Snackbar.make(view, "Please Enter the Stock Code", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    sendMail(DEVELOPER_EMAIL,"Feedback",null);
                }
            });
        }
        Button btn_yl_fb = (Button) findViewById(R.id.btn_yl);
        Button btn_wc_fb = (Button) findViewById(R.id.btn_wc);
        btn_yl_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent facebookIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("fb://profile/yinli.cristiano"));
//                PackageManager packageManager = getPackageManager();
//                List<ResolveInfo> activities = packageManager.queryIntentActivities(facebookIntent, 0);
//                boolean isIntentSafe = (activities.size() > 0);
//                if (isIntentSafe) {
//                    startActivity(facebookIntent);
//                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.facebook.com/yinli.cristiano")));
//                }
            }
        });
        btn_wc_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent facebookIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("fb://profile/cong.wang.37604"));
//                PackageManager packageManager = getPackageManager();
//                List<ResolveInfo> activities = packageManager.queryIntentActivities(facebookIntent, 0);
//                boolean isIntentSafe = (activities.size() > 0);
//                if (isIntentSafe) {
//                    startActivity(facebookIntent);
//                } else {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.facebook.com/cong.wang.37604")));
//                }
            }
        });
    }

    private void showTemplatePage(final String[] templateData) {
        this.currentPage = TEMPLATE_PAGE;
        setContentView(R.layout.activity_template);
        final EditText et_name = (EditText) findViewById(R.id.et_name);
        final EditText et_subject = (EditText) findViewById(R.id.et_subject);
        final EditText et_content = (EditText) findViewById(R.id.et_content);


        if(templateData != null){
            et_name.setText(templateData[0]);
            et_subject.setText(templateData[1]);
            et_content.setText(templateData[2]);
        } else {
            et_name.setText("New Template");
        }

        FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        if (fab_send != null) {
            fab_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   sendMail(null,et_subject.getText().toString(),et_content.getText().toString());
                }
            });
        }


        FloatingActionButton fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        if (fab_save != null) {
            fab_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] data = new String[3];
                    data[0] = et_name.getText().toString();
                    data[1] = et_subject.getText().toString();
                    data[2] = et_content.getText().toString();
                    saveTemplate(data);
                }
            });
        }

        displayMenus();

    }

    private void showLoadPage() {
        this.currentPage = LOAD_PAGE;
        isDeleteMode = false;
        setContentView(R.layout.activity_load);
        displayMenus();

        Cursor cursor = myDB.selectAll();
        if(cursor.getCount() == 0){
            Toast.makeText(getApplicationContext(),"No Template Found!",Toast.LENGTH_LONG).show();
            showTemplatePage(null);
            return;
        }
        String[] data = new String[cursor.getCount()];
        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToNext();
            data[i] = cursor.getString(0);
        }

        TemplateListAdapter adapter = new TemplateListAdapter(data,getApplicationContext());
        ListView lv_template = (ListView) findViewById(R.id.list_template);
        lv_template.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position) instanceof String){
                    final String selection = (String) parent.getItemAtPosition(position);
                    if(isDeleteMode){
                        deleteTemplate(selection);

                    } else {
                        showTemplatePage(getTemplate(selection));
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"SYSTEM ERROR: TEMP_NAME_WRONG_FORMAT\nPlease Contact Developer.",Toast.LENGTH_LONG).show();
                    Log.d("TEST","Template name should be string");
                }

            }
        });

        lv_template.setAdapter(adapter);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_delete);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDeleteMode = !isDeleteMode;
                    if(isDeleteMode){
                        fab.setImageResource(R.drawable.ic_bar_cancel);
                        Toast.makeText(MainActivity.this,"Please select the template you want to delete.",Toast.LENGTH_SHORT).show();
                    } else {
                        fab.setImageResource(R.drawable.ic_bar_delete);
                    }
                }
            });
        }
        FloatingActionButton fab_create = (FloatingActionButton) findViewById(R.id.fab_create);
        if (fab_create != null) {
            fab_create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTemplatePage(null);
                }
            });
        }




    }

    public void initialSampleTemplate() {
        String[] templateInfo;
        InputStream is = getResources().openRawResource(R.raw.sample);


        try {
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                templateInfo = line.split(this.SPLIT_TAG);
                if (templateInfo.length != 3) {
                    Log.d("ERROR", "Sample Template Format Wrong!");
                    continue;
                }
                templateInfo[2] = templateInfo[2].replace(this.ENTER_TAG, "\n");

                if (myDB.insert(myDB.TEMPLATES_TABLE, templateInfo)) {
                    count++;
                }
            }
            Log.d("TEST", count + " entries added");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getTemplate(String templateName) {
        Cursor cursor = myDB.select(templateName);
        if (cursor.getCount() != 1) {
            return null;
        }
        String[] template = new String[DatabaseHelper.TEMPLATES_TABLE_COLUMNS];
        if (cursor.moveToNext()) {
            for (int i = 0; i < DatabaseHelper.TEMPLATES_TABLE_COLUMNS; i++) {
                template[i] = cursor.getString(i);
            }
        }
        return template;
    }

    private void deleteTemplate(final String templateName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure to DELETE template:"+templateName+" ?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (myDB.delete(templateName) == 1) {
                    Toast.makeText(getApplicationContext(),"Template: <"+templateName+"> has been deleted!",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Deleting failed!",Toast.LENGTH_LONG).show();
                }
                showLoadPage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.show();
        isDeleteMode = false;
    }

    private void saveTemplate(final String[] templateData){
        final String templateName = templateData[0];
        if(!myDB.insert(myDB.TEMPLATES_TABLE,templateData)){

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Warning");
            builder.setMessage(templateName+" already exists, do you want to overwrite it?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (myDB.update(templateData)) {
                        Toast.makeText(getApplicationContext(),"Template: <"+templateName+"> has been saved!",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Saving failed!",Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setCancelable(true);
            builder.show();
        } else {
            Toast.makeText(getApplicationContext(),"Template: <"+templateName+"> has been saved!",Toast.LENGTH_LONG).show();
        }
    }

    private void displayMenus(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }
    public void sendMail(String[] receiver,String subject, String content){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(emailIntent.EXTRA_EMAIL, receiver);
        emailIntent.putExtra(emailIntent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(emailIntent.EXTRA_TEXT, content);
        emailIntent.setType("message/rfc822");
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(emailIntent, 0);
        boolean isIntentSafe = (activities.size() > 0);
        if (isIntentSafe) {
            startActivity(emailIntent);
        } else {
            Toast.makeText(MainActivity.this,"You don't have an e-mail app installed yet!",Toast.LENGTH_LONG).show();
        }
    }
}
