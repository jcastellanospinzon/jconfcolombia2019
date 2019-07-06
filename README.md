# JConf Colombia 2019: Java & AWS - Son una hermosa pareja


Estos son los ejemplos mostrados en mi conferencia en la JConf Colombia 2019.

> ADVERTENCIA: La ejecución de los scripts de AWS Cloudformation presentes en los ejemplos implican la creación de 
> recursos de AWS los cuales PUEDEN incurrir en costos asociados. Para estar seguro de no hacer nada indebido (y quedar
> empeñado con AWS) bien pueden revisar la capa gratuita de servicios y revisar que las instancias creadas siempre
> sean instancias elegibles para ser gratuitas. También recomiendo eliminar los stacks una vez se haya hecho el
> experimento de ejecución.

## Pasos para ejecutar los ejemplos

### Prerequisitos

* Java 8 y Maven (Duh!).

* Tener una cuenta en AWS (Duh!).

* Elegir la región en la que se van a trabajar los ejemplos (tener en cuenta que los scripts fueron diseñados para 
funcionar en **us-east-2 - _Ohio_** sin embargo las modificaciones para funcionar en otra región son mínimas).

* En el servicio EC2 de la región elegida se debe haber creado un **Key Pair**. Esta es la llave que permitirá acceder a
las instancias a través de SSH. Se recomienda tener las precauciones necesarias con el archivo descargado.

* En el servicio VPC de la región elegida se debe tener creada una VPC y al menos una Subnet para trabajar. Normalmente
dichos recursos son creados automáticamente por defecto.

* Se debe tener un bucket en la región elegida, en dicho bucket se alojarán los artefactos compilados de los proyectos
ejemplo.

### Ahora sí manos a la obra

1. Compilar los proyectos:

   - [serverless-aws-helloworld](./serverless-aws-helloworld)
   - [springboot-helloworld](./springboot-helloworld)
   - [springboot-tomcat-helloworld](./springboot-tomcat-helloworld)

   Basta con ubicarse en las carpetas correspondientes y hacer: `mvn clean install` en cada uno.

2. Subir los artefactos producidos por la compilación al bucket mencionado en los prerequisitos. Los archivos son:

   - [serverless-aws-helloworld-1.0.0.jar][3]
   - [springboot-helloworld-1.0.0.jar][1]
   - [springboot-tomcat-helloworld-1.0.0.war][2]

   Los archivos deben hacerse públicos en el bucket para que Cloudformation pueda acceder a ellos fácilmente.

#### Ejemplo con EC2

En este ejemplo la idea es crear una instancia EC2, dentro de dicha instancia instalar el JDK 8 (AWS Corretto) y luego,
subir a la instancia un ejecutable de SpringBoot ([springboot-helloworld-1.0.0.jar][1]) que es nuestra aplicación.

##### Ajustes del script:
* En la línea 69 del script, si se ha cambiado el nombre del artefacto, reemplazar por la ubicación del artefacto en 
nuestro bucket:
   ```
   "source": "https://URL pública del artefacto"
   ```

Una vez que el script esté listo, se debe crear un nuevo Stack en Cloudformation utilizando el [template.json][4] del 
proyecto [cloudformation-ec2](./cloudformation-ec2)
   
##### Parámetros del script:
* **KeyName**: Debe ser el Key Pair mencionado en los prerequisitos.
* **ImageId**: Imagen AMI para crear la instancia. El script utiliza por defecto una imagen **Amazon Linux 2 64-bit 
x86** para la región **us-east-2**. Si se desea usar otro tipo, cambiar el valor por defecto (puede incurrir en gastos).
* **InstanceType**: Tipo de instancia EC2 a utilizar. El script utiliza por defecto una imagen **t2.micro**. Si se desea
usar otro tipo, cambiar el valor por defecto (puede incurrir en gastos).
* **ServerVPC**: Debe ser la VPC mencionada en los prerequisitos.
* **ServerSubnet**: Debe ser la Subnet mencionada en los prerequisitos.
* **SSHLocation**: Direcciones IP desde las cuales se podrá acceder la máquina por SSH, se recomienda usar únicamente
su IP pública **xxx.yyy.zzz.www/0**.
* **Bucket**: El nombre del bucket nombrado en los prerequisitos.

Cuando el stack se crea exitosamente, podemos dirigirnos al tab de Outputs y en el valor de **ServerURL** se encontrará
la URL para acceder a nuestra aplicación de ejemplo.

#### Ejemplo con Elastic Beanstalk

En este ejemplo la idea es crear una aplicación de Elastic Beanstalk con Java 8 y Tomcat y desplegar nuestro artefacto
([springboot-tomcat-helloworld-1.0.0.jar][2]) que es nuestra aplicación.

##### Ajustes del script:
* En las líneas 68 del script, si se ha cambiado el nombre del artefacto, reemplazar por la ubicación del artefacto
en nuestro bucket:
   ```
   "S3Key" : "NOMBRE DEL ARCHIVO DENTRO DEL BUCKET"
   ```

Una vez que el script esté listo, se debe crear un nuevo Stack en Cloudformation utilizando el [template.json][5] del 
proyecto [cloudformation-elasticbeanstalk](./cloudformation-elasticbeanstalk)
   
##### Parámetros del script:
* **KeyName**: Debe ser el Key Pair mencionado en los prerequisitos.
* **InstanceType**: Tipo de instancia EC2 a utilizar. El script utiliza por defecto una imagen **t2.micro**. Si se desea
usar otro tipo, cambiar el valor por defecto (puede incurrir en gastos).
* **Bucket**: El nombre del bucket nombrado en los prerequisitos.

Cuando el stack se crea exitosamente, podemos dirigirnos al tab de Outputs y en el valor de **ServerURL** se encontrará
la URL para acceder a nuestra aplicación de ejemplo.

#### Ejemplo con Serverless Framework (AWS Lambda + AWS API Gateway)

En este ejemplo la idea es crear una aplicación Serverless que conta de una función AWS Lambda en Java y un AWS API 
Gateway que servirá de entrypoint a la función, y luego, subir a la función un jar 
([serverless-aws-helloworld-1.0.0.jar][3]) que es el código de nuestra función.

##### Ajustes del script:
* En las líneas 67 y 68 del script, si se ha cambiado el nombre del artefacto, reemplazar por la ubicación del artefacto
en nuestro bucket:
   ``` 
   Bucket: NOMBRE DEL BUCKET
   Key: NOMBRE DEL ARCHIVO DENTRO DEL BUCKET
   ```

Una vez que el script esté listo, se debe crear un nuevo Stack en Cloudformation utilizando el [template.json][6] del 
proyecto [cloudformation-serverless](./cloudformation-serverless)
   
##### Parámetros del script:
* **Bucket**: El nombre del bucket nombrado en los prerequisitos.

Cuando el stack se crea exitosamente, podemos dirigirnos al tab de Outputs y en el valor de **ApiUrl** se encontrará
la URL para acceder a nuestra aplicación de ejemplo.

[1]: ./springboot-helloworld/target/springboot-helloworld-1.0.0.jar
[2]: ./springboot-tomcat-helloworld/target/springboot-tomcat-helloworld-1.0.0.war
[3]: ./serverless-aws-helloworld/target/serverless-aws-helloworld-1.0.0.jar

[4]: ./cloudformation-ec2/template.json
[5]: ./cloudformation-elasticbeanstalk/template.json
[6]: ./cloudformation-serverless/template.yml
