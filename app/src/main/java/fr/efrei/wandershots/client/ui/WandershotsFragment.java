package fr.efrei.wandershots.client.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.CredentialsManager;
import fr.efrei.wandershots.client.entities.User;


public abstract class WandershotsFragment<T extends ViewBinding> extends Fragment {

    protected Handler handler;
    protected T binding;
    protected String TAG;
    private CredentialsManager credentialsManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        TAG = this.getClass().getSimpleName();
        credentialsManager = CredentialsManager.getInstance(getContext());
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
            if (types.length > 0 && types[0] instanceof Class) {
                Class<T> bindingClass = (Class<T>) types[0];
                try {
                    Method inflateMethod = bindingClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                    binding = (T) inflateMethod.invoke(null, inflater, container, false);
                    return binding.getRoot();
                } catch (Exception e) {
                    logError("Failed to inflate binding class", e);
                    throw new RuntimeException("Failed to inflate binding class", e);
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void navigateToFragment(Fragment fragment) {
        navigateToFragment(fragment, true);
    }

    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    public void popBackStack() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        }
    }

    public void showToastMessage(int resId) {
        handler.post(() -> Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show());
    }

    protected void logError(String message, Throwable e) {
        Log.e(TAG, message, e);
    }

    protected void debug(String message) {
        Log.d(TAG, message);
    }

    @Nullable
    protected User getCurrentUser() {
        return credentialsManager.getCredentialsFromCache();
    }
}