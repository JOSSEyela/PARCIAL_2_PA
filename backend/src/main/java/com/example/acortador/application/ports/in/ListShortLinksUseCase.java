package com.example.acortador.application.ports.in;

import com.example.acortador.application.dto.LinkListResult;

public interface ListShortLinksUseCase {

    /** Primera consulta: MongoDB (y se guarda en caché). Siguientes: Redis mientras no expire. */
    LinkListResult listAll();
}
