// Generated by view binder compiler. Do not edit!
package com.aplicacion.permisapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.aplicacion.permisapp.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class StatusTramiteCardviewBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final CheckBox AcepttRHcbx;

  @NonNull
  public final TextView JItxt;

  @NonNull
  public final TextView RHtxt;

  @NonNull
  public final TextView SAtxt;

  @NonNull
  public final CheckBox VoBoJIcbx;

  @NonNull
  public final CheckBox VoBoSAcbx;

  @NonNull
  public final TextView fechaHistorial;

  @NonNull
  public final ConstraintLayout progresscl;

  @NonNull
  public final LinearProgressIndicator progresssBar;

  @NonNull
  public final CheckBox sendcbx;

  @NonNull
  public final TextView sendtxt;

  @NonNull
  public final TextView statusHistorial;

  @NonNull
  public final TextView tipoincidenciaHistorial;

  private StatusTramiteCardviewBinding(@NonNull CardView rootView, @NonNull CheckBox AcepttRHcbx,
      @NonNull TextView JItxt, @NonNull TextView RHtxt, @NonNull TextView SAtxt,
      @NonNull CheckBox VoBoJIcbx, @NonNull CheckBox VoBoSAcbx, @NonNull TextView fechaHistorial,
      @NonNull ConstraintLayout progresscl, @NonNull LinearProgressIndicator progresssBar,
      @NonNull CheckBox sendcbx, @NonNull TextView sendtxt, @NonNull TextView statusHistorial,
      @NonNull TextView tipoincidenciaHistorial) {
    this.rootView = rootView;
    this.AcepttRHcbx = AcepttRHcbx;
    this.JItxt = JItxt;
    this.RHtxt = RHtxt;
    this.SAtxt = SAtxt;
    this.VoBoJIcbx = VoBoJIcbx;
    this.VoBoSAcbx = VoBoSAcbx;
    this.fechaHistorial = fechaHistorial;
    this.progresscl = progresscl;
    this.progresssBar = progresssBar;
    this.sendcbx = sendcbx;
    this.sendtxt = sendtxt;
    this.statusHistorial = statusHistorial;
    this.tipoincidenciaHistorial = tipoincidenciaHistorial;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static StatusTramiteCardviewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static StatusTramiteCardviewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.status_tramite_cardview, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static StatusTramiteCardviewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.AcepttRHcbx;
      CheckBox AcepttRHcbx = ViewBindings.findChildViewById(rootView, id);
      if (AcepttRHcbx == null) {
        break missingId;
      }

      id = R.id.JItxt;
      TextView JItxt = ViewBindings.findChildViewById(rootView, id);
      if (JItxt == null) {
        break missingId;
      }

      id = R.id.RHtxt;
      TextView RHtxt = ViewBindings.findChildViewById(rootView, id);
      if (RHtxt == null) {
        break missingId;
      }

      id = R.id.SAtxt;
      TextView SAtxt = ViewBindings.findChildViewById(rootView, id);
      if (SAtxt == null) {
        break missingId;
      }

      id = R.id.VoBoJIcbx;
      CheckBox VoBoJIcbx = ViewBindings.findChildViewById(rootView, id);
      if (VoBoJIcbx == null) {
        break missingId;
      }

      id = R.id.VoBoSAcbx;
      CheckBox VoBoSAcbx = ViewBindings.findChildViewById(rootView, id);
      if (VoBoSAcbx == null) {
        break missingId;
      }

      id = R.id.fechaHistorial;
      TextView fechaHistorial = ViewBindings.findChildViewById(rootView, id);
      if (fechaHistorial == null) {
        break missingId;
      }

      id = R.id.progresscl;
      ConstraintLayout progresscl = ViewBindings.findChildViewById(rootView, id);
      if (progresscl == null) {
        break missingId;
      }

      id = R.id.progresssBar;
      LinearProgressIndicator progresssBar = ViewBindings.findChildViewById(rootView, id);
      if (progresssBar == null) {
        break missingId;
      }

      id = R.id.sendcbx;
      CheckBox sendcbx = ViewBindings.findChildViewById(rootView, id);
      if (sendcbx == null) {
        break missingId;
      }

      id = R.id.sendtxt;
      TextView sendtxt = ViewBindings.findChildViewById(rootView, id);
      if (sendtxt == null) {
        break missingId;
      }

      id = R.id.statusHistorial;
      TextView statusHistorial = ViewBindings.findChildViewById(rootView, id);
      if (statusHistorial == null) {
        break missingId;
      }

      id = R.id.tipoincidenciaHistorial;
      TextView tipoincidenciaHistorial = ViewBindings.findChildViewById(rootView, id);
      if (tipoincidenciaHistorial == null) {
        break missingId;
      }

      return new StatusTramiteCardviewBinding((CardView) rootView, AcepttRHcbx, JItxt, RHtxt, SAtxt,
          VoBoJIcbx, VoBoSAcbx, fechaHistorial, progresscl, progresssBar, sendcbx, sendtxt,
          statusHistorial, tipoincidenciaHistorial);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}