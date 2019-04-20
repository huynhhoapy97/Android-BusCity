package truonghuynhhoa.ptit.buscity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import truonghuynhhoa.ptit.adapter.InstructionAdapter;
import truonghuynhhoa.ptit.model.Instruction;

public class InstructionActivity extends AppCompatActivity {

    private String origin, destination, stationOrigin, stationDestination;
    private ArrayList<Instruction> listInstruction;
    private ArrayList<Integer> listNumericalOrderOrigin, listNumericalOrderDestination, listTurn;
    private TextView txtOrigin, txtDestination;
    private ListView lvInstruction;
    private InstructionAdapter instructionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        addControls();
        addEvents();
    }

    private void addControls() {

        txtOrigin = findViewById(R.id.txtOrigin);
        txtDestination = findViewById(R.id.txtDestination);
        lvInstruction = findViewById(R.id.lvInstruction);
        instructionAdapter = new InstructionAdapter(InstructionActivity.this, R.layout.item_instruction);

        lvInstruction.setAdapter(instructionAdapter);

        // Lấy dữ liệu từ Find gửi qua
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        origin = bundle.getString("origin");
        destination = bundle.getString("destination");
        listInstruction = (ArrayList<Instruction>) bundle.getSerializable("listInstruction");
        listNumericalOrderOrigin = (ArrayList<Integer>) bundle.getSerializable("listNumericalOrderOrigin");
        listNumericalOrderDestination = (ArrayList<Integer>) bundle.getSerializable("listNumericalOrderDestination");
        listTurn = (ArrayList<Integer>) bundle.getSerializable("listTurn");
        stationOrigin = bundle.getString("stationOrigin");
        stationDestination = bundle.getString("stationDestination");

        instructionAdapter.clear();
        instructionAdapter.addAll(listInstruction);

        Toast.makeText(InstructionActivity.this, "" + listInstruction.size(), Toast.LENGTH_SHORT).show();
        Toast.makeText(InstructionActivity.this, origin, Toast.LENGTH_SHORT).show();
        Toast.makeText(InstructionActivity.this, destination, Toast.LENGTH_SHORT).show();

        txtOrigin.setText(origin);
        txtDestination.setText(destination);
    }

    private void addEvents() {
        lvInstruction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Instruction instruction = (Instruction) parent.getItemAtPosition(position);
                int numericalOrderOrigin = listNumericalOrderOrigin.get(position);
                int numericalOrderDestination = listNumericalOrderDestination.get(position);
                int turn = listTurn.get(position);

                Toast.makeText(InstructionActivity.this, "origin: " + numericalOrderOrigin, Toast.LENGTH_SHORT).show();
                Toast.makeText(InstructionActivity.this, "destination: " + numericalOrderDestination, Toast.LENGTH_SHORT).show();
                Toast.makeText(InstructionActivity.this, "turn: " + turn, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(InstructionActivity.this, DetailInstructionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("origin", origin);
                bundle.putString("destination", destination);
                bundle.putInt("busRoute", instruction.getBusRoute());
                bundle.putInt("walkDistance", instruction.getWalkDistance());
                bundle.putInt("busDistance", instruction.getBusDistance());
                bundle.putInt("numericalOrderOrigin", numericalOrderOrigin);
                bundle.putInt("numericalOrderDestination", numericalOrderDestination);
                bundle.putInt("turn", turn);
                bundle.putString("stationOrigin", stationOrigin);
                bundle.putString("stationDestination", stationDestination);

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }
}
