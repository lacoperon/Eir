from django.db import models
from PIL import Image, ImageDraw
from PIL import Image as PILImage


class Image(models.Model):
    """
    Represents one of the available images.
    """

    # The filename of the image
    filename = models.FilePathField()

    def get_image_data(self):
        return PILImage.open(self.filename)


class Instruction(models.Model):

    # The main image
    image = models.ForeignKey(Image)

    # When was this instruction created.
    creation_date = models.DateTimeField(auto_now_add=True)

    @staticmethod
    def latest_instruction():
        return Instruction.objects.order_by('-creation_date')[0:1].get()

    def render_instruction(self):
        """
        Renders the instruction. I.e. puts the drawings on top of the image.
        Returns a path to a png image with the overlaid drawings.
        """
        base_image = self.image.get_image_data()

        # Add all the drawings on top.
        for drawing in self.drawing_set.all():
            drawing.draw(base_image)

        # Write to temporal file.
        path = '/tmp/file.png'
        base_image.save(path, "PNG")

        return path


class Drawing(models.Model):

    # The shape of this drawing
    shape = models.TextField(max_length=40)

    # Location on the image
    location_x = models.IntegerField()
    location_y = models.IntegerField()

    # The instruction to which this thing belongs to.
    instruction = models.ForeignKey(Instruction)

    def draw(self, image):
        """
        Draws itself on the given image.
        """

        # Just draw a line for the moment.
        im = ImageDraw.Draw(image)
        im.line((0, 0, 100, 100), fill=128)

