from django.conf.urls import include, url
from django.contrib import admin

from main import views

urlpatterns = [
    url(r'^admin/', include(admin.site.urls)),

    url(r'^image', views.get_image),
    url(r'^api/instruction/latest', views.get_latest_instruction),
    url(r'^api/instruction/create', views.create_new_instruction),
    url(r'^api/instruction/drawing/add', views.add_drawing),
    url(r'^$', views.main_app),
]
