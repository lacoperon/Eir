from django.conf.urls import include, url
from django.contrib import admin

from main import views

urlpatterns = [
    url(r'^admin/', include(admin.site.urls)),

    url(r'^image', views.get_image),
    url(r'^update-images', views.update_images),
    url(r'^api/instruction/latest', views.get_latest_instruction),
    url(r'^api/instruction/create', views.create_new_instruction),
    url(r'^api/instruction/reset', views.reset),
    url(r'^$', views.main_app),
]
