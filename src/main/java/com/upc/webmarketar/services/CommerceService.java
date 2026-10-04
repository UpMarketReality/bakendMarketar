package com.upc.webmarketar.services;

import com.upc.webmarketar.dtos.AddItemRequest;
import com.upc.webmarketar.dtos.CartDTO;
import com.upc.webmarketar.dtos.PurchaseDTO;

import java.util.List;

public interface CommerceService {
    ResultDTO<FavoriteDTO> addFavorite(long product);

    List<FavoriteDTO> favorites();

    void removeFavorite(long product);

    CartDTO cart();

    ResultDTO<CartDTO> add(AddItemRequest r);

    CartDTO quantity(long item, int quantity);

    void remove(long item);

    ResultDTO<PurchaseDTO> checkout(String key, Long expectedCart);

    PurchaseDTO purchase(long id);

    PageDTO<PurchaseDTO> purchases(int page, int size);
}
