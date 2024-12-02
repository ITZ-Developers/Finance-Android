package com.finance.ui.key.filter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.finance.BR;
import com.finance.R;
import com.finance.constant.Constants;
import com.finance.data.model.api.response.key.KeyGroupResponse;
import com.finance.data.model.api.response.organization.OrganizationResponse;
import com.finance.databinding.ActivityKeyFilterBinding;
import com.finance.di.component.ActivityComponent;
import com.finance.ui.base.BaseActivity;
import com.finance.ui.key.filter.adapter.KeyGroupSelectAdapter;
import com.finance.ui.key.filter.adapter.KindSelectAdapter;
import com.finance.ui.key.filter.adapter.OrganizationSelectAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyFilterActivity extends BaseActivity<ActivityKeyFilterBinding, KeyFilterViewModel> {

    private KeyGroupSelectAdapter keyGroupSelectAdapter;
    private OrganizationSelectAdapter organizationSelectAdapter;
    private KindSelectAdapter kindSelectAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_key_filter;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromIntent();
        setupKinds();
        setupKeyGroups();
        setupOrganizations();
        setupSwipeFreshLayout();
        //Set default category
        selectCategory(1);
        viewBinding.rcvSelects.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            viewModel.keyGroupId.set(bundle.getLong(Constants.KEY_GROUP_ID));
            viewModel.organizationId.set(bundle.getLong(Constants.ORGANIZATION_ID));
            viewModel.kind.set(bundle.getInt(Constants.KIND));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearFilter(){
        //Clear kind
        viewModel.kind.set(0);
        kindSelectAdapter.setIsSelected(getResources().getString(R.string.all));
        kindSelectAdapter.notifyDataSetChanged();
        //Clear key group
        viewModel.keyGroupId.set(0L);
        keyGroupSelectAdapter.setIsSelected(0L);
        keyGroupSelectAdapter.notifyDataSetChanged();
        //Clear organization
        viewModel.organizationId.set(0L);
        organizationSelectAdapter.setIsSelected(0L);
        organizationSelectAdapter.notifyDataSetChanged();
    }
    private void setupSwipeFreshLayout() {
        viewBinding.swipeLayout.setEnabled(true);
        viewBinding.swipeLayout.setOnRefreshListener(() -> {
            selectCategory(viewModel.category.get());
            viewBinding.swipeLayout.setRefreshing(false);
        });
    }
    public void selectCategory(Integer category) {
        viewModel.category.set(category);
        switch (category) {
            case 1:
                viewBinding.rcvSelects.setAdapter(kindSelectAdapter);
                break;
            case 2:
                viewBinding.rcvSelects.setAdapter(keyGroupSelectAdapter);
                break;
            case 3:
                viewBinding.rcvSelects.setAdapter(organizationSelectAdapter);
                break;
            default:
                break;
        }
    }

    private void setupOrganizations() {
        viewModel.getAllOrganizations();
        //Get Data from call API
        viewModel.organizationResponses.observe(this, organizationResponses -> {
            if (organizationResponses != null) {
                organizationSelectAdapter = new OrganizationSelectAdapter(organizationResponses, new OrganizationSelectAdapter.OrganizationSelectListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void itemClick(View view, int position) {
                        viewModel.organizationId.set(organizationResponses.get(position).getId());
                        organizationSelectAdapter.setIsSelected(organizationResponses.get(position).getId());
                        organizationSelectAdapter.notifyDataSetChanged();
                    }
                });
                organizationSelectAdapter.setIsSelected(viewModel.organizationId.get());
                organizationSelectAdapter.getOrganizations().add(0, new OrganizationResponse(0L, getResources().getString(R.string.all), null));
                organizationSelectAdapter.notifyItemChanged(0);
            }
        });
    }

    private void setupKeyGroups() {
        viewModel.getAllKeyGroups();
        //Get Data from call API
        viewModel.keyGroupResponses.observe(this, keyGroupResponses -> {
            if (keyGroupResponses != null) {
                keyGroupSelectAdapter = new KeyGroupSelectAdapter(keyGroupResponses, new KeyGroupSelectAdapter.KeyGroupSelectListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void itemClick(View view, int position) {
                        viewModel.keyGroupId.set(keyGroupResponses.get(position).getId());
                        keyGroupSelectAdapter.setIsSelected(keyGroupResponses.get(position).getId());
                        keyGroupSelectAdapter.notifyDataSetChanged();
                    }
                });
                keyGroupSelectAdapter.setIsSelected(viewModel.keyGroupId.get());
                keyGroupSelectAdapter.getKeyGroups().add(0, new KeyGroupResponse(0L, getResources().getString(R.string.all), ""));
                keyGroupSelectAdapter.notifyItemChanged(0);
            }
        });
    }

    private void setupKinds() {
        List<String> kinds = new ArrayList<>();
        kinds.add(getResources().getString(R.string.all));
        kinds.add(getResources().getString(R.string.server));
        kinds.add(getResources().getString(R.string.web));

        kindSelectAdapter = new KindSelectAdapter(kinds, new KindSelectAdapter.KindSelectListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void itemClick(View view, int position) {
                viewModel.kind.set(position);
                kindSelectAdapter.setIsSelected(kinds.get(position));
                kindSelectAdapter.notifyDataSetChanged();
            }
        });
        kindSelectAdapter.setIsSelected(kinds.get(Objects.requireNonNull(viewModel.kind.get())));
    }

    public void filter(){
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.KEY_GROUP_ID, Objects.requireNonNull(viewModel.keyGroupId.get()));
        bundle.putLong(Constants.ORGANIZATION_ID, Objects.requireNonNull(viewModel.organizationId.get()));
        bundle.putInt(Constants.KIND, Objects.requireNonNull(viewModel.kind.get()));
        resultIntent.putExtras(bundle);
        setResult(Constants.FILTER, resultIntent);
        finish();
    }
}
