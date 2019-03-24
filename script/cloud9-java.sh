
# Install Java 1.8
sudo echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update -y
sudo apt-get install -y oracle-java8-installer
sudo rm -rf /var/lib/apt/lists/*
sudo rm -rf /var/cache/oracle-jdk8-installer

# Define commonly used JAVA_HOME variable and update PATH
echo 'export JAVA_HOME=/usr/lib/jvm/java-8-oracle' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc 
java -version

# Install Maven2
sudo apt-add-repository universe
sudo apt-get update
sudo apt-get remove maven2 -y
sudo apt-get install maven -y