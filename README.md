GoogleSearches-Android-App
==========================

Author: Leonid Sukharnikov

This is an Android application that saves Google searches into a list of sorted and tagged searches. Target API=19
In addition, the contents of each search can be shared (facebook, google mail, etc), edited or deleted.

The app utilizes the following classes and interfaces:
import android.os.Bundle;
import android.app.ListActivity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
