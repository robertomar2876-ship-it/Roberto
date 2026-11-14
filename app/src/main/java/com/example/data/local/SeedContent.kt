package com.example.data.local

import com.example.data.model.ArticleEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.EducationalResourceEntity

object SeedContent {

    fun categories(): List<CategoryEntity> {
        val videoCats = listOf(
            "Inteligencia emocional",
            "Ansiedad y estrés",
            "Depresión y bienestar emocional",
            "Autoestima y autoconocimiento",
            "Relaciones interpersonales",
            "Psicología educativa",
            "Psicología organizacional",
            "Salud mental",
            "Habilidades sociales",
            "Desarrollo personal",
            "Trastornos psicológicos",
            "Neuropsicología"
        ).map { CategoryEntity(name = it, scope = "VIDEO", isSystemDefault = true) }

        val articleCats = listOf(
            "Inteligencia emocional",
            "Neuropsicología",
            "Ansiedad y estrés",
            "Autoestima y autoconocimiento",
            "Psicología educativa",
            "Psicología organizacional",
            "Salud mental"
        ).map { CategoryEntity(name = it, scope = "ARTICLE", isSystemDefault = true) }

        val resourceCats = listOf(
            "Inteligencia emocional",
            "Autoconocimiento",
            "Técnicas de respiración",
            "Autoestima",
            "Manejo del estrés",
            "Infografías psicológicas"
        ).map { CategoryEntity(name = it, scope = "RESOURCE", isSystemDefault = true) }

        val galleryCats = listOf(
            "Jornadas psicoeducativas",
            "Material visual y diagramas",
            "Talleres de bienestar",
            "Salud mental comunitaria"
        ).map { CategoryEntity(name = it, scope = "GALLERY", isSystemDefault = true) }

        return videoCats + articleCats + resourceCats + galleryCats
    }

    fun articles(now: Long): List<ArticleEntity> = listOf(
        ArticleEntity(
            title = "Comprender y regular las emociones desde la inteligencia emocional",
            category = "Inteligencia emocional",
            bundledIllustrationKey = "balance",
            isFeatured = true,
            publishedAtMillis = now - 86_400_000L * 2,
            introduction = "Las emociones cumplen funciones adaptativas esenciales: nos informan sobre necesidades, límites y significados en nuestro entorno. Comprenderlas es el primer paso para responder con flexibilidad en lugar de reaccionar de forma automática.",
            bodyContent = "Desde el modelo de habilidad de Mayer y Salovey (1997), la inteligencia emocional integra cuatro capacidades interrelacionadas: percibir emociones con precisión, utilizarlas para facilitar el pensamiento, comprender sus transiciones y regularlas de manera reflexiva.\n\nA menudo confundimos regular una emoción con suprimirla. La evidencia empírica en regulación emocional (Gross, 2015) indica que la supresión expresiva aumenta la activación fisiológica, mientras que la reevaluación cognitiva y el etiquetado emocional reducen la intensidad del malestar sin negar lo que sentimos.",
            conclusions = "Nombrar con precisión lo que experimentamos, identificar el detonante y elegir una respuesta coherente con nuestros valores fortalece la autonomía emocional y la salud interpersonal.",
            apaReferences = "Gross, J. J. (2015). Emotion regulation: Current status and future prospects. Psychological Inquiry, 26(1), 1–26.\nMayer, J. D., & Salovey, P. (1997). What is emotional intelligence? En P. Salovey & D. J. Sluyter (Eds.), Emotional development and emotional intelligence (pp. 3–31). Basic Books."
        ),
        ArticleEntity(
            title = "Neurobiología del estrés y estrategias de afrontamiento saludable",
            category = "Ansiedad y estrés",
            bundledIllustrationKey = "brain",
            isFeatured = true,
            publishedAtMillis = now - 86_400_000L * 5,
            introduction = "El estrés no es intrínsecamente negativo; constituye una respuesta psicofisiológica ante demandas percibidas que superan temporalmente nuestros recursos habituales.",
            bodyContent = "Cuando percibimos una amenaza o sobrecarga, el eje hipotálamo-hipófisis-suprarrenal (HHA) y el sistema nervioso simpático movilizan energía mediante adrenalina y cortisol. A corto plazo, esta respuesta favorece la atención y la acción.\n\nSin embargo, cuando el estrés se vuelve crónico sin periodos de recuperación, se deteriora la calidad del sueño, la memoria de trabajo y el equilibrio inmunológico. El modelo transaccional de Lazarus y Folkman (1984) distingue entre afrontamiento centrado en el problema (modificar la situación) y centrado en la emoción (regular el impacto interno).",
            conclusions = "Combinar pausas activas, respiración lenta, establecimiento de límites y reestructuración de demandas permite que el sistema nervioso retorne a su línea base de seguridad.",
            apaReferences = "Lazarus, R. S., & Folkman, S. (1984). Stress, appraisal, and coping. Springer.\nMcEwen, B. S. (2007). Physiology and neurobiology of stress and adaptation: Central role of the brain. Physiological Reviews, 87(3), 873–904."
        ),
        ArticleEntity(
            title = "Distorsiones cognitivas: cómo nuestros pensamientos moldean el ánimo",
            category = "Salud mental",
            bundledIllustrationKey = "hero",
            isFeatured = true,
            publishedAtMillis = now - 86_400_000L * 9,
            introduction = "En momentos de vulnerabilidad emocional, la mente tiende a procesar la información mediante atajos rígidos conocidos como sesgos o distorsiones cognitivas.",
            bodyContent = "Aaron Beck describió cómo pensamientos automáticos negativos —como la catastrofización, la lectura de mente, el pensamiento dicotómico ('todo o nada') y la sobregeneralización— intensifican la tristeza y la ansiedad.\n\nIdentificar un pensamiento como hipótesis y no como hecho indiscutible abre espacio para buscar evidencia a favor y en contra, formulando interpretaciones más realistas y compasivas.",
            conclusions = "El registro de pensamientos y el cuestionamiento socrático constituyen herramientas psicoeducativas valiosas para reducir la rumiación cotidiana.",
            apaReferences = "Beck, J. S. (2020). Cognitive behavior therapy: Basics and beyond (3rd ed.). Guilford Press.\nOrganización Mundial de la Salud. (2022). Informe mundial sobre salud mental: Transformar la salud mental para todos."
        ),
        ArticleEntity(
            title = "Autoestima realista y autocompasión frente a la autocrítica severa",
            category = "Autoestima y autoconocimiento",
            bundledIllustrationKey = "balance",
            isFeatured = false,
            publishedAtMillis = now - 86_400_000L * 14,
            introduction = "Construir una relación sana con uno mismo no implica perfección constante ni comparación social, sino reconocimiento honesto de fortalezas y limitaciones.",
            bodyContent = "Mientras la autoestima contingente depende de logros externos o aprobación continua, la autocompasión propuesta por Kristin Neff (2003) integra tres pilares: amabilidad hacia uno mismo en momentos de error, humanidad compartida (comprender que errar es parte de la experiencia humana) y atención plena frente al dolor.\n\nDiversos estudios muestran que el diálogo interno compasivo favorece la perseverancia y reduce la procrastinación asociada al miedo al fracaso.",
            conclusions = "Tratarse con el mismo respeto que ofreceríamos a un amigo cercano fortalece la resiliencia psicológica.",
            apaReferences = "Neff, K. D. (2003). Self-compassion: An alternative conceptualization of a healthy attitude toward oneself. Self and Identity, 2(2), 85–101.\nRosenberg, M. (1965). Society and the adolescent self-image. Princeton University Press."
        ),
        ArticleEntity(
            title = "Higiene del sueño y funciones ejecutivas en estudiantes universitarios",
            category = "Psicología educativa",
            bundledIllustrationKey = "brain",
            isFeatured = false,
            publishedAtMillis = now - 86_400_000L * 19,
            introduction = "Dormir adecuadamente no es tiempo perdido: durante el sueño se consolidan aprendizajes, se depuran metabolitos cerebrales y se recalibra la reactividad emocional.",
            bodyContent = "La privación parcial crónica de sueño reduce la eficiencia de la corteza prefrontal, afectando la atención sostenida, la planificación académica y el control inhibitorio.\n\nMantener horarios regulares al despertar, evitar cafeína en la tarde y asociar la cama exclusivamente con el descanso son medidas respaldadas por la investigación clínica en medicina conductual del sueño.",
            conclusions = "Proteger entre 7 y 9 horas de descanso nocturno mejora tanto el rendimiento académico como la estabilidad afectiva.",
            apaReferences = "Walker, M. (2017). Why we sleep: Unlocking the power of sleep and dreams. Scribner.\nAmerican Psychiatric Association. (2022). DSM-5-TR."
        ),
        ArticleEntity(
            title = "Prevención del desgaste laboral (burnout) en entornos organizacionales",
            category = "Psicología organizacional",
            bundledIllustrationKey = "hero",
            isFeatured = false,
            publishedAtMillis = now - 86_400_000L * 24,
            introduction = "La OMS reconoce el síndrome de desgaste ocupacional (burnout) en la CIE-11 como un fenómeno vinculado al estrés laboral crónico que no ha sido gestionado con éxito.",
            bodyContent = "Maslach y Leiter (2016) identifican tres dimensiones centrales: agotamiento emocional, cinismo o distanciamiento mental del trabajo, y sensación de ineficacia profesional.\n\nSu prevención requiere intervenciones tanto organizacionales (cargas justas, claridad de roles, liderazgo saludable) como individuales (desconexión digital, descanso y límites claros).",
            conclusions = "Promover culturas laborales psicológicamente seguras protege la salud mental y la sostenibilidad humana en las organizaciones.",
            apaReferences = "Maslach, C., & Leiter, M. P. (2016). Understanding the burnout experience: Recent research and its implications for psychiatry. World Psychiatry, 15(2), 103–111.\nOrganización Mundial de la Salud. (2022). CIE-11: QD85 Síndrome de desgaste ocupacional."
        )
    )

    data class ResourceSeedSpec(
        val fileName: String,
        val title: String,
        val description: String,
        val category: String,
        val resourceType: String,
        val sections: List<Pair<String, String>>,
        val apaReference: String
    )

    fun resourceSpecs(): List<ResourceSeedSpec> = listOf(
        ResourceSeedSpec(
            fileName = "guia_inteligencia_emocional.pdf",
            title = "Guía práctica de inteligencia emocional",
            description = "Documento PDF educativo para identificar, nombrar y regular emociones cotidianas.",
            category = "Inteligencia emocional",
            resourceType = "Guía",
            sections = listOf(
                "1. ¿Qué es la inteligencia emocional?" to "Es la capacidad de percibir, comprender, utilizar y regular nuestras propias emociones y reconocer las de otras personas con empatía y respeto.",
                "2. Semáforo emocional en 3 pasos" to "• Detente: observa sensaciones físicas (tensión, calor, respiración).\n• Nombra: distingue si sientes frustración, tristeza, miedo, alegría o calma.\n• Elige: selecciona una acción saludable en lugar de responder por impulso.",
                "3. Registro diario sugerido" to "Anota: Situación -> Pensamiento automático -> Emoción (0-10) -> Respuesta alternativa saludable."
            ),
            apaReference = "Mayer, J. D., & Salovey, P. (1997). What is emotional intelligence? Basic Books."
        ),
        ResourceSeedSpec(
            fileName = "ejercicios_autoconocimiento.pdf",
            title = "Cuaderno de ejercicios de autoconocimiento",
            description = "Preguntas reflexivas sobre valores personales, límites y fortalezas.",
            category = "Autoconocimiento",
            resourceType = "Ejercicio",
            sections = listOf(
                "1. Clarificación de valores" to "Escribe tres áreas importantes en tu vida actual (familia, formación, salud, amistades) y qué acción pequeña refleja tus valores en cada una.",
                "2. Mapa de límites saludables" to "Identifica situaciones que drenan tu energía y redacta frases claras y asertivas en primera persona ('Necesito...', 'Prefiero...').",
                "3. Reconocimiento de fortalezas" to "Enumera cinco recursos personales que te hayan ayudado a superar retos previos."
            ),
            apaReference = "Hayes, S. C., Strosahl, K. D., & Wilson, K. G. (2012). Acceptance and commitment therapy (2nd ed.). Guilford Press."
        ),
        ResourceSeedSpec(
            fileName = "tecnicas_respiracion_regulacion.pdf",
            title = "Técnicas de respiración diafragmática y 4-7-8",
            description = "Protocolo paso a paso para activar la respuesta de calma fisiológica.",
            category = "Técnicas de respiración",
            resourceType = "Respiración",
            sections = listOf(
                "1. Respiración diafragmática lenta" to "Coloca una mano en el pecho y otra en el abdomen. Inhala por la nariz en 4 segundos llevando el aire hacia el abdomen, sostén 2 segundos y exhala suavemente por la boca en 6 segundos.",
                "2. Técnica 4-7-8" to "Inhala en 4 segundos, mantén el aire 7 segundos sin tensión y exhala de forma continua durante 8 segundos. Repite de 3 a 4 ciclos.",
                "3. Recomendación de seguridad" to "Si sientes mareo leve al iniciar, vuelve a tu ritmo natural. Practica primero en momentos de calma."
            ),
            apaReference = "Zaccaro, A., et al. (2018). How breath-control can change your life: A systematic review. Frontiers in Human Neuroscience, 12, 353."
        ),
        ResourceSeedSpec(
            fileName = "actividades_autoestima.pdf",
            title = "Actividades para fortalecer la autoestima y autocompasión",
            description = "Ejercicios prácticos para transformar la autocrítica severa en diálogo constructivo.",
            category = "Autoestima",
            resourceType = "Material",
            sections = listOf(
                "1. Carta desde la compasión" to "Describe una dificultad actual como si le escribieras a alguien que aprecias mucho, reconociendo su esfuerzo y humanidad.",
                "2. Evidencia de logros cotidianos" to "Al finalizar cada día, registra dos acciones valiosas que realizaste sin minimizar tu mérito.",
                "3. Separar conducta de identidad" to "Reemplaza etiquetas globales ('Soy un fracaso') por descripciones específicas ('Hoy cometí un error y puedo aprender de él')."
            ),
            apaReference = "Neff, K. D. (2003). Self-compassion: An alternative conceptualization of a healthy attitude toward oneself. Self and Identity, 2(2), 85–101."
        ),
        ResourceSeedSpec(
            fileName = "manejo_del_estres_academico.pdf",
            title = "Manual breve de manejo del estrés y prevención del agotamiento",
            description = "Estrategias basadas en evidencia para organizar cargas, descansar y pedir apoyo.",
            category = "Manejo del estrés",
            resourceType = "PDF",
            sections = listOf(
                "1. Diferenciar lo controlable de lo incontrolable" to "Divide tus preocupaciones en dos columnas: acciones concretas bajo tu control hoy vs. factores externos que requieren aceptación.",
                "2. Pausas de recuperación cognitiva" to "Por cada 50 minutos de trabajo concentrado, realiza 5-10 minutos sin pantallas para hidratarte y estirar.",
                "3. Higiene del sueño" to "Mantén horarios estables y evita revisar pendientes justo antes de acostarte."
            ),
            apaReference = "Lazarus, R. S., & Folkman, S. (1984). Stress, appraisal, and coping. Springer."
        ),
        ResourceSeedSpec(
            fileName = "infografia_regulacion_emocional.pdf",
            title = "Infografía educativa: Ciclo de pensamiento, emoción y conducta",
            description = "Resumen visual del modelo cognitivo-conductual y técnica de anclaje 5-4-3-2-1.",
            category = "Infografías psicológicas",
            resourceType = "Infografía",
            sections = listOf(
                "Modelo Cognitivo-Conductual (ABC)" to "A (Situación) -> B (Interpretación / Pensamiento) -> C (Emoción y Conducta). Modificar nuestra interpretación cambia la respuesta emocional.",
                "Anclaje sensorial 5-4-3-2-1" to "Identifica: 5 cosas que ves, 4 que sientes al tacto, 3 sonidos que escuchas, 2 aromas que percibes y 1 sabor o respiración consciente.",
                "Recordatorio psicoeducativo" to "Este material apoya el bienestar diario y no sustituye valoración clínica profesional."
            ),
            apaReference = "Beck, J. S. (2020). Cognitive behavior therapy: Basics and beyond (3rd ed.). Guilford Press."
        )
    )
}
