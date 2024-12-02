package com.finance.ui.key.group;

import androidx.databinding.ObservableField;

import com.finance.MVVMApplication;
import com.finance.data.Repository;
import com.finance.data.model.api.ResponseListObj;
import com.finance.data.model.api.ResponseStatus;
import com.finance.data.model.api.ResponseWrapper;
import com.finance.data.model.api.response.key.KeyGroupResponse;
import com.finance.ui.base.BaseViewModel;

import io.reactivex.rxjava3.core.Observable;

public class KeyGroupViewModel extends BaseViewModel{

    public ObservableField<Integer> pageNumber = new ObservableField<>(0);
    public ObservableField<Integer> pageSize = new ObservableField<>(20);

    public ObservableField<Integer> totalElement = new ObservableField<>();
    public ObservableField<Integer> positionUC = new ObservableField<>();
    public ObservableField<Boolean> isValidKey = new ObservableField<>(false);
    public KeyGroupViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public Observable<ResponseWrapper<ResponseListObj<KeyGroupResponse>>> getKeyGroups(){
        return repository.getApiService().getKeyGroupList(pageNumber.get(), pageSize.get());
    }

    public Observable<ResponseStatus> deleteKeyGroup(Long id){
        return repository.getApiService().deleteKeyGroup(id);
    }
    public Observable<ResponseWrapper<KeyGroupResponse>> getKeyGroup(Long id){
        return repository.getApiService().getKeyGroupDetails(id);
    }
}
