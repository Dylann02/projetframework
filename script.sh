#!/bin/bash

# ==========================================
# CONFIGURATION
# Mettez ici le chemin absolu ou relatif vers le dossier 'lib' de votre projet de test
# ==========================================
PROJET_TEST_LIB="/Users/dylan/Documents/App/S5/SPRING/exoFramework/test/lib"

# 1. Nettoyage des anciens dossiers de build et du jar précédent
echo "🧹 Nettoyage..."
rm -rf build
rm -f framework.jar

# 2. Création du dossier temporaire pour les fichiers compilés (.class)
mkdir build

# 3. Compilation du fichier Java
echo "⚙️ Compilation de FrontControllerServlet.java..."
javac -cp "lib/*" -d build src/**/*.java

# On vérifie si la compilation a réussi
if [ $? -eq 0 ]; then
    echo "✅ Compilation réussie !"
    
    # 4. Création du fichier .jar
    echo "📦 Création du fichier framework.jar..."
    jar -cvf framework.jar -C build .
    
    # 5. Copie directe du JAR dans le dossier lib du projet de test
    echo "🚀 Envoi du JAR vers le projet de test..."
    if [ -d "$PROJET_TEST_LIB" ]; then
        cp -f framework.jar "$PROJET_TEST_LIB/"
        echo "🎉 Terminé ! Ton fichier framework.jar est prêt et copié dans le projet de test."
    else
        echo "⚠️ Attention : Le dossier $PROJET_TEST_LIB n'existe pas. Le JAR reste à la racine."
    fi
else
    echo "❌ Erreur lors de la compilation."
    exit 1
fi