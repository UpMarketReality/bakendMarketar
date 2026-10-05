package com.upc.webmarketar.services;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.security.dtos.*;

import java.util.List;

public interface QuoteService {
    QuoteRequestDTO create(CreateQuoteRequest r);

    QuoteRequestDTO ownRequest(long id);

    QuoteRequestDTO sellerRequest(long id);

    PageDTO<QuoteRequestDTO> requests(boolean buyer, Long category, int page, int size);

    OfferDTO offer(long request, OfferRequest r);

    List<OfferDTO> offers(long request);

    ResultDTO<OrderDTO> accept(long offer);

    QuoteRequestDTO cancel(long id);

    OrderDTO order(long id, boolean seller);

    PageDTO<OrderDTO> orders(boolean seller, int page, int size);

    OrderDTO status(long id, String next);
}

