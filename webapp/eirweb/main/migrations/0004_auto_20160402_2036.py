# -*- coding: utf-8 -*-
# Generated by Django 1.9.5 on 2016-04-02 20:36
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0003_auto_20160402_1625'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='drawing',
            name='instruction',
        ),
        migrations.RemoveField(
            model_name='instruction',
            name='image',
        ),
        migrations.AddField(
            model_name='instruction',
            name='filepath',
            field=models.FilePathField(default='images/1.png'),
            preserve_default=False,
        ),
        migrations.DeleteModel(
            name='Drawing',
        ),
    ]
