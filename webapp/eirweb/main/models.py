from django.db import models
from PIL import Image as PILImage


class Image(models.Model):
    """
    Represents one of the available images.
    """

    # The filename of the image
    filename = models.FilePathField()

    tags = models.ManyToManyField("Tag")

    @staticmethod
    def get_relevant_images():
        # TODO!
        return Image.objects.all()

    def get_image_data(self):
        return PILImage.open(self.filename)


class Tag(models.Model):
    """
    Tag of an image
    """
    title = models.TextField(max_length=40)


class Instruction(models.Model):

    # The main image
    filepath = models.FilePathField()

    # When was this instruction created.
    creation_date = models.DateTimeField(auto_now_add=True)

    @staticmethod
    def latest_instruction():
        return Instruction.objects.order_by('-creation_date')[0:1].get()

    def render_instruction(self):
        """
        For the moment just returns the image.
        """
        return self.filepath

