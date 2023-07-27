// Generated by view binder compiler. Do not edit!
package com.aplicacion.permisapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.aplicacion.permisapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainIrseAntesBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final AutoCompleteTextView autoCompleteTextView;

  @NonNull
  public final Button buttonsalirantes;

  @NonNull
  public final TextInputEditText editTextTextPersonName2;

  @NonNull
  public final TextInputEditText horaFinalSalir;

  @NonNull
  public final TextInputEditText horaInicialSalir;

  @NonNull
  public final TextInputEditText seleccionarfechaSalir;

  @NonNull
  public final TextInputLayout textInputLayout;

  private ActivityMainIrseAntesBinding(@NonNull ScrollView rootView,
      @NonNull AutoCompleteTextView autoCompleteTextView, @NonNull Button buttonsalirantes,
      @NonNull TextInputEditText editTextTextPersonName2, @NonNull TextInputEditText horaFinalSalir,
      @NonNull TextInputEditText horaInicialSalir, @NonNull TextInputEditText seleccionarfechaSalir,
      @NonNull TextInputLayout textInputLayout) {
    this.rootView = rootView;
    this.autoCompleteTextView = autoCompleteTextView;
    this.buttonsalirantes = buttonsalirantes;
    this.editTextTextPersonName2 = editTextTextPersonName2;
    this.horaFinalSalir = horaFinalSalir;
    this.horaInicialSalir = horaInicialSalir;
    this.seleccionarfechaSalir = seleccionarfechaSalir;
    this.textInputLayout = textInputLayout;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainIrseAntesBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainIrseAntesBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main_irse_antes, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainIrseAntesBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.autoCompleteTextView;
      AutoCompleteTextView autoCompleteTextView = ViewBindings.findChildViewById(rootView, id);
      if (autoCompleteTextView == null) {
        break missingId;
      }

      id = R.id.buttonsalirantes;
      Button buttonsalirantes = ViewBindings.findChildViewById(rootView, id);
      if (buttonsalirantes == null) {
        break missingId;
      }

      id = R.id.editTextTextPersonName2;
      TextInputEditText editTextTextPersonName2 = ViewBindings.findChildViewById(rootView, id);
      if (editTextTextPersonName2 == null) {
        break missingId;
      }

      id = R.id.hora_final_salir;
      TextInputEditText horaFinalSalir = ViewBindings.findChildViewById(rootView, id);
      if (horaFinalSalir == null) {
        break missingId;
      }

      id = R.id.hora_inicial_salir;
      TextInputEditText horaInicialSalir = ViewBindings.findChildViewById(rootView, id);
      if (horaInicialSalir == null) {
        break missingId;
      }

      id = R.id.seleccionarfecha_salir;
      TextInputEditText seleccionarfechaSalir = ViewBindings.findChildViewById(rootView, id);
      if (seleccionarfechaSalir == null) {
        break missingId;
      }

      id = R.id.textInputLayout;
      TextInputLayout textInputLayout = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout == null) {
        break missingId;
      }

      return new ActivityMainIrseAntesBinding((ScrollView) rootView, autoCompleteTextView,
          buttonsalirantes, editTextTextPersonName2, horaFinalSalir, horaInicialSalir,
          seleccionarfechaSalir, textInputLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
