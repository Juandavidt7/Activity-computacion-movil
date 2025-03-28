package co.edu.uniminuto.secondactivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTask;
    private Button btnAdd;
    private ListView listTasks;
    private SearchView searchTasks;
    private ArrayAdapter<String> adapter;
    private List<String> tasks;

    private static final String TASK_PREFS = "TaskPrefs";
    private static final String TASK_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // componentes
        editTask = findViewById(R.id.editTask);
        btnAdd = findViewById(R.id.btnAdd);
        listTasks = findViewById(R.id.listTasks);
        searchTasks = findViewById(R.id.searchTasks);

        // tareas guardadas
        tasks = new ArrayList<>();
        loadTasks();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        listTasks.setAdapter(adapter);

        // Configurar el botón para agregar o actualizar
        btnAdd.setOnClickListener(v -> addOrUpdateTask());

        // Configurar la búsqueda
        searchTasks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // Clic en los elementos del ListView
        listTasks.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTask = tasks.get(position);
            showEditDeleteDialog(position, selectedTask);
        });
    }

    private void addOrUpdateTask() {
        String task = editTask.getText().toString().trim();

        if (!TextUtils.isEmpty(task)) {
            if (btnAdd.getText().toString().equalsIgnoreCase("Agregar Tarea")) {
                // Modo agregar
                tasks.add(task);
                Toast.makeText(this, "Tarea agregada", Toast.LENGTH_SHORT).show();
            } else {
                // Modo editar
                int position = (int) btnAdd.getTag(); // Recupera la posición de la tarea a editar
                tasks.set(position, task);
                btnAdd.setText("Agregar Tarea"); // Cambiar el texto del botón
                btnAdd.setTag(null); // Limpiar la posición
                Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
            }

            // Guardar cambios
            saveTasks();

            // Actualizar lista de tareas
            adapter.notifyDataSetChanged();
            editTask.setText("");
        } else {
            Toast.makeText(this, "Ingrese una tarea", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDeleteDialog(int position, String task) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Opciones de tarea");
        builder.setMessage("¿Qué desea hacer con esta tarea?");

        builder.setPositiveButton("Editar", (dialog, which) -> {
            editTask.setText(task); // Mostrar tarea en el campo de texto
            btnAdd.setText("Actualizar"); // Cambiar texto del botón
            btnAdd.setTag(position); // Guardar la posición en el Tag
        });

        builder.setNegativeButton("Eliminar", (dialog, which) -> {
            tasks.remove(position); // Eliminar tarea
            saveTasks(); // Guardar cambios
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void saveTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences(TASK_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder taskString = new StringBuilder();
        for (String task : tasks) {
            taskString.append(task).append(";");
        }
        editor.putString(TASK_KEY, taskString.toString());
        editor.apply();
    }

    private void loadTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences(TASK_PREFS, MODE_PRIVATE);
        String taskString = sharedPreferences.getString(TASK_KEY, "");

        if (!TextUtils.isEmpty(taskString)) {
            String[] savedTasks = taskString.split(";");
            tasks.clear();
            tasks.addAll(Arrays.asList(savedTasks));
        }
    }
}
