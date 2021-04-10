package com.example.pronotes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pronotes.auth.Login;
import com.example.pronotes.auth.Register;
import com.example.pronotes.note.AddNote;
import com.example.pronotes.note.EditNote;
import com.example.pronotes.note.Note;
import com.example.pronotes.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static com.example.pronotes.R.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //layout
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteLists;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        drawerLayout = findViewById(R.id.drawer);
        noteLists = findViewById(R.id.noteLists);
        nav_view = findViewById(id.nav_view);
        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);


        //firebase
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        //firebase sa hogi
        Query query = fStore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        //data ko collect karaga note adapter pass
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
                noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {

                @Override
                protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                //data note class main ma
                //user na store kiya in note act then it extract from there and come here
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());

                //generate random color & then set in notedetail back
                final int code = getRandomColor();//call
                noteViewHolder.mCardview.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));

                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                //data pass in notedetail class
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("code", code);
                        i.putExtra("noteId",docId);
                        v.getContext().startActivity(i);

                    }
                });
                //3 dot ko declare kiya hai
                ImageView menuIcon = noteViewHolder.view.findViewById(id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        //docid to get user firebase id
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        //below sa show hoga
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(), EditNote.class);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                i.putExtra("noteId", docId);
                                startActivity(i);
                                return false;
                            }
                        });
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //note Deleted
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error In Deleting Note", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                return false;
                            }
                        });
                        menu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent share = new Intent(Intent.ACTION_SEND);
                                String sharebody = note.getContent();
                                share.setType("text/plain");
                                share.putExtra(Intent.EXTRA_TEXT, sharebody);
                                startActivity(Intent.createChooser(share, "Shared"));
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        //assign kiya layout recycle ko
        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(noteAdapter);
        View headerView = nav_view.getHeaderView(0);

        //display name & email
        final TextView username = headerView.findViewById(id.userDisplayName);
        TextView userEmail = headerView.findViewById(id.userDisplayEmail);
        Button edit = headerView.findViewById(R.id.edit);
        if(user.isAnonymous() ){
            userEmail.setVisibility(View.GONE);
            username.setText("Temprary User Is Login");
        }else{
            userEmail.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }


        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddNote.class));
                overridePendingTransition(anim.slide_up, anim.slide_down);
            }
        });

        //user to change(name)
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getLayoutInflater().inflate(layout.custom_alert, null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Enter Name");
                alertDialog.setCancelable(true);

                final EditText etComments = (EditText) view.findViewById(R.id.etComments);

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Temprary Change", Toast.LENGTH_SHORT).show();
                        username.setText(etComments.getText());
                    }
                });
                alertDialog.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();
            }
        });
    }
//side bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //bec when we select we dont need drawer view know
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case id.shareapp:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            case id.rateapp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=PackageName")));
                break;
            case id.notes:
                break;
            case id.addnotes:
                startActivity(new Intent(this, AddNote.class));
                overridePendingTransition(anim.slide_up, anim.slide_down);
                break;
            case id.syn:
                if (user.isAnonymous()){
                    startActivity(new Intent(this, Login.class));
                    }else{
                    Toast.makeText(this, "You Are Registered", Toast.LENGTH_SHORT).show(); }
                    break;
            case id.logout:
                checkUser();
                break;
            default:
                Toast.makeText(this,"Comming Soon",Toast.LENGTH_SHORT).show();
                }
                return false;
                }
//alert when user nav to login using drawer
    private void checkUser() {
        if (user.isAnonymous()) {
            displayAlert();
        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Splash.class));
            overridePendingTransition(anim.slide_up, anim.slide_down);
            finish();
        }
    }
    //login & logout
    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are You Sure ?")
                .setMessage("Logout Will Delete All Your Temprary Account Data If You Don't Want To Delete Start New Accouunt")
                .setPositiveButton("New Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        overridePendingTransition(anim.slide_up, anim.slide_down);
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),Splash.class));
                                overridePendingTransition(anim.slide_up, anim.slide_down);
                                finish();
                            }
                        });
                    }
                });
        warning.show();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings)
        {
            Toast.makeText(this,"Setting Menu Is Clicked",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
        }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements Filterable {
        TextView noteTitle, noteContent;
        ArrayList<TextView> list =new ArrayList<TextView>();
        View view;
        CardView mCardview;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            view = itemView;
            mCardview = itemView.findViewById(R.id.noteCard);
            list.add(noteTitle);
        }
        @Override
        public Filter getFilter() {
            return null;
        }
    }
        private int getRandomColor() {
            List<Integer> colorCode = new ArrayList<>();
            colorCode.add(R.color.blue);
            colorCode.add(R.color.yellow);
            colorCode.add(R.color.skyblue);
            colorCode.add(R.color.lightPurple);
            colorCode.add(R.color.lightGreen);
            colorCode.add(R.color.gray);
            colorCode.add(R.color.pink);
            colorCode.add(R.color.red);
            colorCode.add(R.color.greenlight);
            colorCode.add(R.color.notgreen);
            //obj create kiya hai
            Random randomColor = new Random();
            int number = randomColor.nextInt(colorCode.size());
            return colorCode.get(number);
        }
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

}
