package org.projectakshara.android.finman;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListBillsActivity extends Activity {
	private BillsDataSource datasource;
	BillAdapter adapter;
	Activity a;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listbills);
		a = this;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        datasource = new BillsDataSource(this.getBaseContext());
        datasource.open();
        List<Bill> values = datasource.getAllBills();
        //ArrayAdapter<Bill> adapter = new ArrayAdapter<Bill>(this, R.layout.billlist_row, values);
        adapter = new BillAdapter(a, values);
        ListView vi = (ListView) findViewById(R.id.listbills);
        vi.setAdapter(adapter);
		vi.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,	int position, long id) {
				Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_listbills, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
      datasource.open();
      super.onResume();
    }

    @Override
    protected void onPause() {
      datasource.close();
      super.onPause();
    }
}
