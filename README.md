## 🚀 Pasos para ejecutar el proyecto
1. Levantar la Base de Datos

a) Descargar docker compose

[Link de descarga](https://docs.docker.com/desktop/setup/install/windows-install/)

b) Ejecuta la app Docker desktop una vez instalada

c) Abri tu terminal o consola. Navega hasta la carpeta principal del proyecto (donde se encuentra el archivo docker-compose.yml)

Ejecuta el siguiente comando:
    ``` 
      docker-compose up -d
    ``` 

<img width="451" height="311" alt="Screenshot 2025-10-08 at 1 23 44 PM" src="https://github.com/user-attachments/assets/99fb4f9b-90f7-4d23-9ee5-7e56a35e7f8f" />

e) - Checkeas que la base de datos esta levantada con el comando 
    ``` 
      docker ps
    ``` 

Tenes que ver algo como

    ``` 
      CONTAINER ID   IMAGE       COMMAND                  CREATED       STATUS       PORTS                               NAMES
      cd9a63a25531   mysql:8.0   "docker-entrypoint.s…"   2 hours ago   Up 2 hours   33060/tcp, 0.0.0.0:3308->3306/tcp   project-mysql
    ``` 
    
<img width="1057" height="183" alt="Screenshot 2025-10-08 at 1 24 05 PM" src="https://github.com/user-attachments/assets/2bbfde34-ccea-4635-a5e1-dfb23cdab46e" />


2 - Levanta el proyecto de spring haciendo run en la class FinalProjectApplication


<img width="907" height="349" alt="Screenshot 2025-10-08 at 1 24 31 PM" src="https://github.com/user-attachments/assets/b69f0836-e7e2-4ebf-a10e-988efcbd8fce" />



### VIDEO TUTORIAL



https://github.com/user-attachments/assets/8aa4be68-4031-4d11-a867-111fd6024b07





