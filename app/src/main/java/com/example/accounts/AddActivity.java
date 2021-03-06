package com.example.accounts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "SuspiciousMethodCalls"})
public class AddActivity extends AppCompatActivity {
    private RelativeLayout rl;
    private LinearLayout ll;
    private ArrayList<RelativeLayout> relativeLayoutsList;
    private User usr;
    private ManageAccount mngAcc;
    private ArrayList<Account> listAccount;
    private AccountElement elem;
    private ArrayList<AccountElement> accountElementsList;
    private EditText name;
    private ArrayList<EditText> email;
    private ArrayList<EditText> user;
    private ArrayList<EditText> password;
    private ArrayList<EditText> description;
    private ArrayList<ImageButton> showPass;
    private int i;
    private boolean b;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar addToolbar = findViewById(R.id.addToolbar);
        User owner = (User) (Objects.requireNonNull(getIntent().getExtras())).get("owner");
        rl = findViewById(R.id.addActivityLay);
        ll = findViewById(R.id.linearLayAdd);
        ManageUser mngUsr = new ManageUser();
        ArrayList<User> listUser = mngUsr.deserializationListUser(this);
        usr = mngUsr.findUser(listUser, Objects.requireNonNull(owner).getUser());
        if (usr != null) {
            addToolbar.setSubtitle("Aggiungi account alla lista di " + usr.getUser());
            setSupportActionBar(addToolbar);
            mngAcc = new ManageAccount();
            listAccount = mngAcc.deserializationListAccount(this, usr.getUser());
            accountElementsList = new ArrayList<>();
            relativeLayoutsList = new ArrayList<>();
            name = findViewById(R.id.nameAddEdit);
            email = new ArrayList<>();
            user = new ArrayList<>();
            password = new ArrayList<>();
            description = new ArrayList<>();
            showPass = new ArrayList<>();
            i = 0;
            moreElem(ll);
            Button addButton = findViewById(R.id.addButton);
            Button emptyButton = findViewById(R.id.emptyButton);
            FloatingActionButton addElem = findViewById(R.id.addElemFloatingButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b = false;
                    name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                    accountElementsList.clear();
                    for (int n = 0; n < i; n++) {
                        email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.colorAccent)));
                        if (!(email.get(n).getText().toString().isEmpty() && user.get(n).getText().toString().isEmpty() && password.get(n).getText().toString().isEmpty() && description.get(n).getText().toString().isEmpty())) {
                            elem = new AccountElement(email.get(n).getText().toString(), user.get(n).getText().toString(), password.get(n).getText().toString(), description.get(n).getText().toString());
                            accountElementsList.add(elem);
                        } else b = true;
                    }
                    Account acc = new Account(name.getText().toString(), accountElementsList);
                    if (acc.getName().isEmpty()) {
                        name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
                        notifyUser("Associare il nome all'account!");
                        return;
                    }
                    int n = 0;
                    for (AccountElement elem : accountElementsList) {
                        if (fieldError(elem, n)) return;
                        n++;
                    }
                    if (mngAcc.notFind(acc, listAccount)) {
                        listAccount.add(acc);
                        mngAcc.serializationListAccount(AddActivity.this, listAccount, usr.getUser());
                        if (b)
                            notifyUser("Gli elementi senza nessun campo compilato non verranno memorizzati!");
                        goToViewActivity(usr);
                    } else {
                        name.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
                        notifyUser("Applicativo già registrato!");
                    }
                }

            });
            emptyButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                    final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    //noinspection deprecation
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    View parent = rl.getRootView();
                    popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                    TextView et = popupView.findViewById(R.id.securityText);
                    et.setText("Sei sicuro di voler resettare tutti i campi?");
                    Button yes = popupView.findViewById(R.id.yes);
                    Button no = popupView.findViewById(R.id.no);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            i = 0;
                            accountElementsList.clear();
                            for (RelativeLayout l : relativeLayoutsList) {
                                ll.removeView(l);
                            }
                            relativeLayoutsList.clear();
                            email.clear();
                            user.clear();
                            password.clear();
                            description.clear();
                            showPass.clear();
                            popupWindow.dismiss();
                            notifyUser("Campi resettati");
                            moreElem(v);
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                }
            });
            addElem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreElem(v);
                }
            });
        } else {
            notifyUser("Utente non rilevato. Impossibile aggiungere l'account.");
            goToMainActivity();
        }
    }


    public void goToMainActivity() {
        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToViewActivity(User usr) {
        Intent intent = new Intent(AddActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        finish();
    }

    private void notifyUser(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    public boolean isInvalidWord(String word) {
        if (word.isEmpty()) return false;
        return !word.matches("[^ ]*");
    }

    public boolean isInvalidEmail(String email) {
        if (email.isEmpty()) return false;
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean fieldError(AccountElement a, int n) {
        if (isInvalidEmail(a.getEmail()) && isInvalidWord(a.getUser()) && isInvalidWord(a.getPassword())) {
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente, Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidWord(a.getPassword())) {
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword()) && isInvalidEmail(a.getEmail())) {
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Email e Password non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser()) && isInvalidEmail(a.getEmail())) {
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campi Utente e Email non validi !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getUser())) {
            user.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Utente non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidEmail(a.getEmail())) {
            email.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Email non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (isInvalidWord(a.getPassword())) {
            password.get(n).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(AddActivity.this, R.color.errorEditText)));
            Toast.makeText(AddActivity.this, "Campo Password non valido !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    public void moreElem(View v) {
        LayoutInflater inflater = LayoutInflater.from(AddActivity.this);
        @SuppressLint("InflateParams") final RelativeLayout relLey = (RelativeLayout) inflater.inflate(R.layout.more_add_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 0);
        relLey.setLayoutParams(params);
        relativeLayoutsList.add(relLey);
        email.add((EditText) relLey.findViewById(R.id.emailAddEdit));
        user.add((EditText) relLey.findViewById(R.id.userAddEdit));
        password.add((EditText) relLey.findViewById(R.id.passAddEdit));
        description.add((EditText) relLey.findViewById(R.id.descriptionAddEdit));
        showPass.add((ImageButton) relLey.findViewById(R.id.showPass));
        (relLey.findViewById(R.id.emailAddImage)).setVisibility(View.GONE);
        (relLey.findViewById(R.id.emailAddEdit)).setVisibility(View.GONE);
        (relLey.findViewById(R.id.userAddImage)).setVisibility(View.GONE);
        (relLey.findViewById(R.id.userAddEdit)).setVisibility(View.GONE);
        (relLey.findViewById(R.id.passAddImage)).setVisibility(View.GONE);
        (relLey.findViewById(R.id.passAddEdit)).setVisibility(View.GONE);
        (relLey.findViewById(R.id.showPass)).setVisibility(View.GONE);
        refreshCardinality();
        ll.addView(relLey);
        final ImageButton show = relLey.findViewById(R.id.showButton);
        final Boolean[] bool = {true};
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bool[0]) {
                    bool[0] = false;
                    show.setImageResource(android.R.drawable.arrow_down_float);
                    (relLey.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.emailAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.showPass)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.showPass)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    bool[0] = true;
                    show.setImageResource(android.R.drawable.arrow_up_float);
                    (relLey.findViewById(R.id.emailAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.showPass)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.showPass)).setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
        (relLey.findViewById(R.id.cardinalityElements)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bool[0]) {
                    bool[0] = false;
                    show.setImageResource(android.R.drawable.arrow_down_float);
                    (relLey.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.emailAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddEdit)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddEdit)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.showPass)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.showPass)).setVisibility(View.VISIBLE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddImage)).animate()
                            .alpha(1.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddImage)).setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    bool[0] = true;
                    show.setImageResource(android.R.drawable.arrow_up_float);
                    (relLey.findViewById(R.id.emailAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.emailAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.emailAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.userAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.userAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddImage)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddImage)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.passAddEdit)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.passAddEdit)).setVisibility(View.GONE);
                                }
                            });
                    (relLey.findViewById(R.id.showPass)).animate()
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    (relLey.findViewById(R.id.showPass)).setVisibility(View.GONE);
                                }
                            });
                }

            }
        });
        ImageButton del = relLey.findViewById(R.id.deleteButton);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = Objects.requireNonNull(layoutInflater).inflate(R.layout.popup_security, (ViewGroup) findViewById(R.id.popupSecurity));
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //noinspection deprecation
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                View parent = rl.getRootView();
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                TextView et = popupView.findViewById(R.id.securityText);
                et.setText("Sei sicuro di voler eliminare " + ((TextView) relLey.findViewById(R.id.cardinalityElements)).getText().toString() + " dai tuoi account?");
                Button yes = popupView.findViewById(R.id.yes);
                Button no = popupView.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        email.remove(relLey.findViewById(R.id.emailAddEdit));
                        user.remove(relLey.findViewById(R.id.userAddEdit));
                        password.remove(relLey.findViewById(R.id.passAddEdit));
                        description.remove(relLey.findViewById(R.id.descriptionAddEdit));
                        showPass.remove(relLey.findViewById(R.id.showPass));
                        relativeLayoutsList.remove(relLey);
                        ll.removeView(relLey);
                        i--;
                        notifyUser(((TextView) relLey.findViewById(R.id.cardinalityElements)).getText().toString() + " è stato rimosso con successo!!");
                        refreshCardinality();
                        popupWindow.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyUser(((TextView) relLey.findViewById(R.id.cardinalityElements)).getText().toString() + " non è stato rimosso.");
                        popupWindow.dismiss();
                    }
                });

            }
        });
        showPass((EditText) relLey.findViewById(R.id.passAddEdit), (ImageButton) relLey.findViewById((R.id.showPass)));
        i++;
    }

    @SuppressLint("SetTextI18n")
    public void refreshCardinality() {
        int c = 0;
        for (RelativeLayout rl : relativeLayoutsList) {
            c++;
            ((TextView) rl.findViewById(R.id.cardinalityElements)).setText(c + "");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showPass(final EditText et, ImageButton showPass) {
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        et.setInputType(InputType.TYPE_NULL);
                        break;
                    case MotionEvent.ACTION_UP:
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(AddActivity.this, ViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("owner", usr);
        startActivity(intent);
        this.finish();
    }
}
