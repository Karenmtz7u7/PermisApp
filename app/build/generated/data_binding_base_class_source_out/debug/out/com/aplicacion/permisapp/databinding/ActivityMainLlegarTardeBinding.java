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

public final class ActivityMainLlegarTardeBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final TextInputEditText HoraFinal;

  @NonNull
  public final AutoCompleteTextView autoCompleteTextView;

  @NonNull
  public final TextInputEditText horaInicial;

  @NonNull
  public final TextInputEditText seleccionarfecha;

  @NonNull
  public final Button sendbtn;

  @NonNull
  public final TextInputLayout textInputLayout;

  @NonNull
  public final TextInputEditText tipoIncidenciatxt;

  private ActivityMainLlegarTardeBinding(@NonNull ScrollView rootView,
      @NonNull TextInputEditText HoraFinal, @NonNull AutoCompleteTextView autoCompleteTextView,
      @NonNull TextInputEditText horaInicial, @NonNull TextInputEditText seleccionarfecha,
      @NonNull Button sendbtn, @NonNull TextInputLayout textInputLayout,
      @NonNull TextInputEditText tipoIncidenciatxt) {
    this.rootView = rootView;
    this.HoraFinal = HoraFinal;
    this.autoCompleteTextView = autoCompleteTextView;
    this.horaInicial = horaInicial;
    this.seleccionarfecha = seleccionarfecha;
    this.sendbtn = sendbtn;
    this.textInputLayout = textInputLayout;
    this.tipoIncidenciatxt = tipoIncidenciatxt;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainLlegarTardeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainLlegarTardeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main_llegar_tarde, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainLlegarTardeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.Hora_final;
      TextInputEditText HoraFinal = ViewBindings.findChildViewById(rootView, id);
      if (HoraFinal == null) {
        break missingId;
      }

      id = R.id.autoCompleteTextView;
      AutoCompleteTextView autoCompleteTextView = ViewBindings.findChildViewById(rootView, id);
      if (autoCompleteTextView == null) {
        break missingId;
      }

      id = R.id.hora_inicial;
      TextInputEditText horaInicial = ViewBindings.findChildViewById(rootView, id);
      if (horaInicial == null) {
        break missingId;
      }

      id = R.id.seleccionarfecha;
      TextInputEditText seleccionarfecha = ViewBindings.findChildViewById(rootView, id);
      if (seleccionarfecha == null) {
        break missingId;
      }

      id = R.id.sendbtn;
      Button sendbtn = ViewBindings.findChildViewById(rootView, id);
      if (sendbtn == null) {
        break missingId;
      }

      id = R.id.textInputLayout;
      TextInputLayout textInputLayout = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout == null) {
        break missingId;
      }

      id = R.id.tipoIncidenciatxt;
      TextInputEditText tipoIncidenciatxt = ViewBindings.findChildViewById(rootView, id);
      if (tipoIncidenciatxt == null) {
        break missingId;
      }

      return new ActivityMainLlegarTardeBinding((ScrollView) rootView, HoraFinal,
          autoCompleteTextView, horaInicial, seleccionarfecha, sendbtn, textInputLayout,
          tipoIncidenciatxt);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
