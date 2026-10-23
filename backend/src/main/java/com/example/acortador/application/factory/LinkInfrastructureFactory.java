package com.example.acortador.application.factory;

import com.example.acortador.application.ports.out.ShortCodeGenerator;
import com.example.acortador.application.ports.out.ShortLinkCache;
import com.example.acortador.application.ports.out.ShortLinkRepository;

/**
 * Abstract Factory: crea una familia coherente de adaptadores de salida
 * (persistencia + caché + generador de códigos).
 *
 * La aplicación solo conoce esta interfaz; las fábricas concretas
 * (MongoDB/Redis o en memoria) viven en infrastructure.factory.
 */
public interface LinkInfrastructureFactory {

    ShortLinkRepository createRepository();

    ShortLinkCache createCache();

    ShortCodeGenerator createCodeGenerator();
}
