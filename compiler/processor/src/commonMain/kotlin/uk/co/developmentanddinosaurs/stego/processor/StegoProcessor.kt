package uk.co.developmentanddinosaurs.stego.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

@OptIn(KspExperimental::class)
class StegoProcessor : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        println("I'm running KSP")
        return emptyList()
    }
}
